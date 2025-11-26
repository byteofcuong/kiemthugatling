package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * Pet Management Scenarios
 * Covers CRUD operations for Pet entity and Pet Types
 * 
 * API Endpoints tested:
 * - POST   /api/owners/{ownerId}/pets      (Create pet for owner)
 * - GET    /api/pets/{petId}                (Get pet by ID)
 * - PUT    /api/pets/{petId}                (Update pet)
 * - DELETE /api/pets/{petId}                (Delete pet)
 * - GET    /api/pettypes                    (Get all pet types)
 * - POST   /api/pettypes                    (Create pet type - Admin)
 * - PUT    /api/pettypes/{id}               (Update pet type - Admin)
 * - DELETE /api/pettypes/{id}               (Delete pet type - Admin)
 */
public class PetScenario {

    /**
     * Scenario 7: Add new pet to an owner
     * Requires: ownerId in session
     */
    public static ChainBuilder createPetForOwner = 
        feed(csv(Constants.PETS_CSV).random())
        .exec(
            http("Create Pet for Owner #{" + Constants.OWNER_ID + "}")
                .post(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}/pets")
                .body(StringBody("{" +
                    "\"name\": \"#{name}\"," +
                    "\"birthDate\": \"#{birthDate}\"," +
                    "\"type\": {\"id\": #{typeId}}" +
                    "}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs(Constants.PET_ID))
                .check(jsonPath("$.name").is("#{name}"))
        );

    /**
     * Scenario 8: Get pet details by ID
     */
    public static ChainBuilder getPetById = 
        exec(
            http("Get Pet by ID: #{" + Constants.PET_ID + "}")
                .get(Constants.PETS_API + "/#{" + Constants.PET_ID + "}")
                .check(status().is(200))
                .check(jsonPath("$.id").exists())
                .check(jsonPath("$.name").exists())
                .check(jsonPath("$.birthDate").exists())
        );

    /**
     * Scenario 9: Update pet information
     */
    public static ChainBuilder updatePet = 
        feed(csv(Constants.PETS_CSV).random())
        .exec(
            http("Update Pet #{" + Constants.PET_ID + "}")
                .put(Constants.PETS_API + "/#{" + Constants.PET_ID + "}")
                .body(StringBody("{" +
                    "\"id\": #{" + Constants.PET_ID + "}," +
                    "\"name\": \"#{name} - Updated\"," +
                    "\"birthDate\": \"#{birthDate}\"," +
                    "\"type\": {\"id\": #{typeId}}" +
                    "}"))
                .check(status().is(204))
        );

    /**
     * Scenario 10: Delete pet
     */
    public static ChainBuilder deletePet = 
        exec(
            http("Delete Pet #{" + Constants.PET_ID + "}")
                .delete(Constants.PETS_API + "/#{" + Constants.PET_ID + "}")
                .check(status().is(204))
        );

    /**
     * Scenario 11: Get all pet types (for dropdown)
     */
    public static ChainBuilder getAllPetTypes = 
        exec(
            http("Get All Pet Types")
                .get("/api/pettypes")
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("petTypeIds"))
                .check(jsonPath("$[*].name").findAll().saveAs("petTypeNames"))
        );

    /**
     * Scenario 12a: Create new pet type (Admin)
     */
    public static ChainBuilder createPetType = 
        exec(session -> {
            String uniqueName = "NewPetType" + System.currentTimeMillis();
            return session.set("newPetTypeName", uniqueName);
        })
        .exec(
            http("Create Pet Type (Admin)")
                .post("/api/pettypes")
                .body(StringBody("{\"name\": \"#{newPetTypeName}\"}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs("newPetTypeId"))
        );

    /**
     * Scenario 12b: Update pet type (Admin)
     */
    public static ChainBuilder updatePetType = 
        exec(
            http("Update Pet Type (Admin)")
                .put("/api/pettypes/#{newPetTypeId}")
                .body(StringBody("{" +
                    "\"id\": #{newPetTypeId}," +
                    "\"name\": \"#{newPetTypeName} - Updated\"" +
                    "}"))
                .check(status().is(204))
        );

    /**
     * Scenario 12c: Delete pet type (Admin)
     */
    public static ChainBuilder deletePetType = 
        exec(
            http("Delete Pet Type (Admin)")
                .delete("/api/pettypes/#{newPetTypeId}")
                .check(status().is(204))
        );

    /**
     * Full Pet CRUD workflow
     * Requires: ownerId in session
     */
    public static ChainBuilder fullPetCRUD = 
        exec(createPetForOwner)
        .pause(1)
        .exec(getPetById)
        .pause(1)
        .exec(updatePet)
        .pause(1)
        .exec(getPetById) // Verify update
        .pause(1)
        .exec(deletePet);

    /**
     * Admin workflow for Pet Types management
     */
    public static ChainBuilder fullPetTypeCRUD = 
        exec(getAllPetTypes)
        .pause(1)
        .exec(createPetType)
        .pause(1)
        .exec(updatePetType)
        .pause(1)
        .exec(deletePetType);
}
