package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * READ-ONLY Scenario - Browse and search operations
 * Simulates users viewing data without making changes
 */
public class BrowseScenario {

    /**
     * Browse Vets - View all veterinarians
     */
    public static ChainBuilder browseVets = 
        exec(
            http("Browse All Vets")
                .get(Constants.VETS_API)
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("vetIds"))
        )
        .pause(2, 4);

    /**
     * Browse Owners - View all pet owners
     */
    public static ChainBuilder browseOwners = 
        exec(
            http("Browse All Owners")
                .get(Constants.OWNERS_API)
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("ownerIds"))
        )
        .pause(2, 3);

    /**
     * Browse Pet Types - View available pet categories
     */
    public static ChainBuilder browsePetTypes = 
        exec(
            http("Browse Pet Types")
                .get("/api/pettypes")
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("petTypeIds"))
        )
        .pause(1, 2);

    /**
     * Full browse flow - User exploring the system
     */
    public static ChainBuilder fullBrowse = 
        exec(browsePetTypes)
        .exec(browseVets)
        .exec(browseOwners);
}
