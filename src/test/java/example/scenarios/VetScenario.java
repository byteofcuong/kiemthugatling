package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * Vet and Specialty Management Scenarios
 * Covers CRUD operations for Vet and Specialty entities
 * 
 * API Endpoints tested:
 * - GET    /api/vets                   (List all vets)
 * - POST   /api/vets                   (Create vet)
 * - GET    /api/vets/{id}              (Get vet by ID)
 * - PUT    /api/vets/{id}              (Update vet)
 * - DELETE /api/vets/{id}              (Delete vet)
 * - GET    /api/specialties            (List specialties)
 * - POST   /api/specialties            (Create specialty)
 * - PUT    /api/specialties/{id}       (Update specialty)
 * - DELETE /api/specialties/{id}       (Delete specialty)
 */
public class VetScenario {

    /**
     * Scenario 13: Get all vets
     */
    public static ChainBuilder getAllVets = 
        exec(
            http("Get All Vets")
                .get(Constants.VETS_API)
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("allVetIds"))
        );

    /**
     * Scenario 14: Create new vet
     */
    public static ChainBuilder createVet = 
        feed(csv(Constants.VETS_CSV).random())
        .exec(
            http("Create Vet")
                .post(Constants.VETS_API)
                .body(StringBody("{" +
                    "\"firstName\": \"#{firstName}\"," +
                    "\"lastName\": \"#{lastName}\"," +
                    "\"specialties\": []" +  // Empty initially
                    "}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs(Constants.VET_ID))
        );

    /**
     * Scenario 15: Assign specialty to vet
     * This updates the vet with specialty information
     */
    public static ChainBuilder assignSpecialtyToVet = 
        // First get available specialties
        exec(
            http("Get All Specialties")
                .get("/api/specialties")
                .check(status().is(200))
                .check(jsonPath("$[0].id").saveAs("specialtyId1"))
                .check(jsonPath("$[1].id").optional().saveAs("specialtyId2"))
        )
        .pause(1)
        // Then assign to vet
        .feed(csv(Constants.VETS_CSV).random())
        .exec(
            http("Assign Specialty to Vet #{" + Constants.VET_ID + "}")
                .put(Constants.VETS_API + "/#{" + Constants.VET_ID + "}")
                .body(StringBody("{" +
                    "\"id\": #{" + Constants.VET_ID + "}," +
                    "\"firstName\": \"#{firstName}\"," +
                    "\"lastName\": \"#{lastName}\"," +
                    "\"specialties\": [{\"id\": #{specialtyId1}}]" +
                    "}"))
                .check(status().is(204))
        );

    /**
     * Scenario 16a: Update vet information
     */
    public static ChainBuilder updateVet = 
        feed(csv(Constants.VETS_CSV).random())
        .exec(
            http("Update Vet #{" + Constants.VET_ID + "}")
                .put(Constants.VETS_API + "/#{" + Constants.VET_ID + "}")
                .body(StringBody("{" +
                    "\"id\": #{" + Constants.VET_ID + "}," +
                    "\"firstName\": \"#{firstName} - Updated\"," +
                    "\"lastName\": \"#{lastName}\"," +
                    "\"specialties\": []" +
                    "}"))
                .check(status().is(204))
        );

    /**
     * Scenario 16b: Delete vet
     */
    public static ChainBuilder deleteVet = 
        exec(
            http("Delete Vet #{" + Constants.VET_ID + "}")
                .delete(Constants.VETS_API + "/#{" + Constants.VET_ID + "}")
                .check(status().is(204))
        );

    /**
     * Scenario 17a: Get all specialties
     */
    public static ChainBuilder getAllSpecialties = 
        exec(
            http("Get All Specialties")
                .get("/api/specialties")
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("specialtyIds"))
        );

    /**
     * Scenario 17b: Create new specialty (Admin)
     */
    public static ChainBuilder createSpecialty = 
        exec(session -> {
            String uniqueName = "NewSpecialty" + System.currentTimeMillis();
            return session.set("newSpecialtyName", uniqueName);
        })
        .exec(
            http("Create Specialty (Admin)")
                .post("/api/specialties")
                .body(StringBody("{\"name\": \"#{newSpecialtyName}\"}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs("newSpecialtyId"))
        );

    /**
     * Scenario 17c: Update specialty (Admin)
     */
    public static ChainBuilder updateSpecialty = 
        exec(
            http("Update Specialty (Admin)")
                .put("/api/specialties/#{newSpecialtyId}")
                .body(StringBody("{" +
                    "\"id\": #{newSpecialtyId}," +
                    "\"name\": \"#{newSpecialtyName} - Updated\"" +
                    "}"))
                .check(status().is(204))
        );

    /**
     * Scenario 17d: Delete specialty (Admin)
     */
    public static ChainBuilder deleteSpecialty = 
        exec(
            http("Delete Specialty (Admin)")
                .delete("/api/specialties/#{newSpecialtyId}")
                .check(status().is(204))
        );

    /**
     * Get vet by ID
     */
    public static ChainBuilder getVetById = 
        exec(
            http("Get Vet by ID: #{" + Constants.VET_ID + "}")
                .get(Constants.VETS_API + "/#{" + Constants.VET_ID + "}")
                .check(status().is(200))
                .check(jsonPath("$.id").exists())
                .check(jsonPath("$.firstName").exists())
        );

    /**
     * Full Vet CRUD workflow
     */
    public static ChainBuilder fullVetCRUD = 
        exec(createVet)
        .pause(1)
        .exec(getVetById)
        .pause(1)
        .exec(assignSpecialtyToVet)
        .pause(1)
        .exec(updateVet)
        .pause(1)
        .exec(deleteVet);

    /**
     * Full Specialty CRUD workflow (Admin)
     */
    public static ChainBuilder fullSpecialtyCRUD = 
        exec(getAllSpecialties)
        .pause(1)
        .exec(createSpecialty)
        .pause(1)
        .exec(updateSpecialty)
        .pause(1)
        .exec(deleteSpecialty);
}
