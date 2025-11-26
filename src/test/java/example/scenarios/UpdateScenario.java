package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * UPDATE Scenario - Modify existing data
 * Simulates users editing owner and pet information
 */
public class UpdateScenario {

    /**
     * Update Owner Information
     * Requires: ownerId in session
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
                    "\"address\": \"#{address} - Updated\"," +
                    "\"city\": \"#{city}\"," +
                    "\"telephone\": \"#{telephone}\"" +
                    "}"))
                .check(status().is(204))
        )
        .pause(1, 2);

    /**
     * Update Pet Information
     * Requires: ownerId and petId in session
     */
    public static ChainBuilder updatePet = 
        exec(
            http("Update Pet #{" + Constants.PET_ID + "}")
                .put(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}/pets/#{" + Constants.PET_ID + "}")
                .body(StringBody("{" +
                    "\"id\": #{" + Constants.PET_ID + "}," +
                    "\"name\": \"Fluffy Updated\"," +
                    "\"birthDate\": \"2023-01-01\"," +
                    "\"type\": {\"id\": 2}" + // Change pet type
                    "}"))
                .check(status().is(204))
        )
        .pause(1, 2);

    /**
     * Create, then Update Owner workflow
     */
    public static ChainBuilder createAndUpdateOwner = 
        exec(OwnerPetScenario.createOwnerAndPet)
        .exec(updateOwner)
        .exec(updatePet);
}
