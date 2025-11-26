package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * SEARCH Scenario - Search and filter operations
 * Simulates users looking for specific data
 */
public class SearchScenario {

    /**
     * Search Owner by Last Name
     */
    public static ChainBuilder searchOwnerByName = 
        feed(csv(Constants.OWNERS_CSV).random())
        .exec(
            http("Search Owner by Last Name: #{lastName}")
                .get(Constants.OWNERS_API)
                .queryParam("lastName", "#{lastName}")
                .check(status().is(200))
                .check(jsonPath("$[0].id").optional().saveAs("foundOwnerId"))
        )
        .pause(1, 3);

    /**
     * Search and View Owner Details
     */
    public static ChainBuilder searchAndViewOwnerDetails = 
        exec(searchOwnerByName)
        .doIf(session -> session.contains("foundOwnerId")).then(
            exec(
                http("View Owner Details - ID: #{foundOwnerId}")
                    .get(Constants.OWNERS_API + "/#{foundOwnerId}")
                    .check(status().is(200))
                    .check(jsonPath("$.firstName").exists())
                    .check(jsonPath("$.pets").exists())
            )
            .pause(2, 4)
        );

    /**
     * View Vet Details
     */
    public static ChainBuilder viewVetDetails = 
        exec(
            http("Get All Vets")
                .get(Constants.VETS_API)
                .check(status().is(200))
                .check(jsonPath("$[0].id").saveAs("vetId"))
        )
        .pause(1)
        .exec(
            http("View Vet Details - ID: #{vetId}")
                .get(Constants.VETS_API + "/#{vetId}")
                .check(status().is(200))
                .check(jsonPath("$.firstName").exists())
                .check(jsonPath("$.specialties").exists())
        )
        .pause(2, 3);
}
