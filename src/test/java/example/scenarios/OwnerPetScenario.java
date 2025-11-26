package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

public class OwnerPetScenario {

    /**
     * Professional Chain Builder with Dynamic Correlation
     * - Data Driven: Uses 5000 unique owners from CSV
     * - Correlation: Extracts Owner ID and uses it for Pet creation
     * - Validation: Checks status codes and response body structure
     * - Realistic: Adds think time between requests
     */
    public static ChainBuilder createOwnerAndPet = 
        // Step 1: Create Owner with dynamic data from CSV
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
                // Validation: Status must be 201 Created
                .check(status().is(201))
                // Correlation: Extract and save Owner ID for next request
                .check(jsonPath("$.id").saveAs(Constants.OWNER_ID))
                // Validation: Verify response contains expected fields
                .check(jsonPath("$.firstName").exists())
                .check(jsonPath("$.lastName").exists())
        )
        // Realistic behavior: User thinks for 1-2 seconds before adding pet
        .pause(1, 2)
        
        // Step 2: Create Pet using the correlated Owner ID
        .exec(
            http("Create Pet for Owner #{" + Constants.OWNER_ID + "}")
                .post(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}/pets")
                .body(StringBody("{" +
                    "\"name\": \"Fluffy\"," +
                    "\"birthDate\": \"2023-01-01\"," +
                    "\"type\": {" +
                        "\"id\": 1" +
                    "}" +
                    "}"))
                // Validation: Status must be 201 Created
                .check(status().is(201))
                // Save Pet ID for potential future use
                .check(jsonPath("$.id").saveAs(Constants.PET_ID))
                // Validation: Verify pet is linked to correct owner
                .check(jsonPath("$.owner.id").is("#{" + Constants.OWNER_ID + "}"))
        );
}
