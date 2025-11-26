package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

public class OwnerPetScenario {

    /**
     * Chain builder that creates an Owner and then a Pet
     * Uses correlation to pass the Owner ID from first request to second request
     */
    public static ChainBuilder createOwnerAndPet = 
        // Step 1: Create Owner
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
        )
        // Step 2: Create Pet using the saved Owner ID
        .exec(
            http("Create Pet")
                .post(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}/pets")
                .body(StringBody("{" +
                    "\"name\": \"Fluffy\"," +
                    "\"birthDate\": \"2023-01-01\"," +
                    "\"type\": {" +
                        "\"id\": 1" +
                    "}" +
                    "}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs(Constants.PET_ID))
        );
}
