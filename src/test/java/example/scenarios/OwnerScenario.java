package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * Owner Management Scenarios
 * Covers all CRUD operations for Owner entity
 * 
 * API Endpoints tested:
 * - GET    /api/owners           (List all)
 * - POST   /api/owners           (Create)
 * - GET    /api/owners/{id}      (Get by ID)
 * - PUT    /api/owners/{id}      (Update)
 * - DELETE /api/owners/{id}      (Delete)
 */
public class OwnerScenario {

    /**
     * Scenario 1: Get all owners (Browse)
     */
    public static ChainBuilder getAllOwners = 
        exec(
            http("Get All Owners")
                .get(Constants.OWNERS_API)
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("allOwnerIds"))
        );

    /**
     * Scenario 2: Create new owner with valid data
     */
    public static ChainBuilder createOwner = 
        feed(csv(Constants.OWNERS_CSV).random())
        .exec(
            http("Create Owner")
                .post(Constants.OWNERS_API)
                .body(StringBody("{" +
                    "\"firstName\": \"#{firstName}\"," +
                    "\"lastName\": \"#{lastName}\"," +
                    "\"address\": \"#{address}\"," +
                    "\"city\": \"#{city}\"," +
                    "\"telephone\": \"#{telephone}\"" +
                    "}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs(Constants.OWNER_ID))
                .check(jsonPath("$.firstName").is("#{firstName}"))
        );

    /**
     * Scenario 3: Get owner by ID
     */
    public static ChainBuilder getOwnerById = 
        exec(
            http("Get Owner by ID: #{" + Constants.OWNER_ID + "}")
                .get(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                .check(status().is(200))
                .check(jsonPath("$.id").exists())
                .check(jsonPath("$.firstName").exists())
                .check(jsonPath("$.lastName").exists())
        );

    /**
     * Scenario 4: Update owner information
     */
    public static ChainBuilder updateOwner = 
        feed(csv(Constants.OWNERS_CSV).random())
        .exec(
            http("Update Owner #{" + Constants.OWNER_ID + "}")
                .put(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                .body(StringBody("{" +
                    "\"id\": #{" + Constants.OWNER_ID + "}," +
                    "\"firstName\": \"#{firstName}\"," +
                    "\"lastName\": \"#{lastName}\"," +
                    "\"address\": \"#{address} - UPDATED\"," +
                    "\"city\": \"#{city}\"," +
                    "\"telephone\": \"#{telephone}\"" +
                    "}"))
                .check(status().is(204))
        );

    /**
     * Scenario 5: Delete owner
     */
    public static ChainBuilder deleteOwner = 
        exec(
            http("Delete Owner #{" + Constants.OWNER_ID + "}")
                .delete(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                .check(status().is(204))
        );

    /**
     * Scenario 6: Create owner with invalid data (Validation Test)
     * Tests: Empty name, invalid phone format
     */
    public static ChainBuilder createOwnerInvalidData = 
        exec(
            http("Create Owner - Empty First Name (Invalid)")
                .post(Constants.OWNERS_API)
                .body(StringBody("{" +
                    "\"firstName\": \"\"," +  // Empty - should fail
                    "\"lastName\": \"TestLast\"," +
                    "\"address\": \"123 Test St\"," +
                    "\"city\": \"TestCity\"," +
                    "\"telephone\": \"1234567890\"" +
                    "}"))
                .check(status().in(400, 422)) // Bad Request or Unprocessable Entity
        )
        .pause(1)
        .exec(
            http("Create Owner - Invalid Phone (Invalid)")
                .post(Constants.OWNERS_API)
                .body(StringBody("{" +
                    "\"firstName\": \"Test\"," +
                    "\"lastName\": \"User\"," +
                    "\"address\": \"123 Test St\"," +
                    "\"city\": \"TestCity\"," +
                    "\"telephone\": \"INVALID\"" +  // Invalid format
                    "}"))
                .check(status().in(400, 422))
        );

    /**
     * Full CRUD workflow for Owner
     */
    public static ChainBuilder fullOwnerCRUD = 
        exec(createOwner)
        .pause(1)
        .exec(getOwnerById)
        .pause(1)
        .exec(updateOwner)
        .pause(1)
        .exec(getOwnerById) // Verify update
        .pause(1)
        .exec(deleteOwner);

    /**
     * Search owner by last name (if API supports query params)
     */
    public static ChainBuilder searchOwnerByLastName = 
        feed(csv(Constants.OWNERS_CSV).random())
        .exec(
            http("Search Owner by Last Name: #{lastName}")
                .get(Constants.OWNERS_API)
                .queryParam("lastName", "#{lastName}")
                .check(status().is(200))
        );
}
