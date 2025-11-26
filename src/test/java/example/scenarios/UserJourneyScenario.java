package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * REALISTIC User Journey - Mixed behaviors
 * Simulates real user patterns with multiple actions
 */
public class UserJourneyScenario {

    /**
     * New Pet Owner Journey
     * User registers as new owner, adds pet, schedules visit
     */
    public static ChainBuilder newPetOwnerJourney = 
        exec(session -> session.set("journeyType", "New Pet Owner"))
        // Step 1: Browse to understand the system
        .exec(BrowseScenario.browsePetTypes)
        .pause(2, 4)
        // Step 2: Register as new owner
        .exec(OwnerPetScenario.createOwnerAndPet)
        .pause(2, 3)
        // Step 3: View own profile
        .exec(
            http("View My Profile")
                .get(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                .check(status().is(200))
        )
        .pause(1, 2)
        // Step 4: Schedule first visit
        .exec(VisitScenario.createVisit)
        .pause(2);

    /**
     * Returning User Journey
     * User searches for their data and updates information
     */
    public static ChainBuilder returningUserJourney = 
        exec(session -> session.set("journeyType", "Returning User"))
        // Step 1: Search for profile
        .exec(SearchScenario.searchOwnerByName)
        .pause(2, 3)
        // Step 2: View visits history
        .exec(VisitScenario.viewAllVisits)
        .pause(2, 4)
        // Step 3: Browse available vets
        .exec(BrowseScenario.browseVets)
        .pause(2);

    /**
     * Admin User Journey
     * Power user performing multiple CRUD operations
     */
    public static ChainBuilder adminUserJourney = 
        exec(session -> session.set("journeyType", "Admin"))
        // Step 1: Browse all data
        .exec(BrowseScenario.fullBrowse)
        .pause(1, 2)
        // Step 2: Create test data
        .exec(OwnerPetScenario.createOwnerAndPet)
        .pause(1)
        // Step 3: Update the data
        .exec(UpdateScenario.updateOwner)
        .pause(1)
        // Step 4: Cleanup (delete)
        .exec(DeleteScenario.createAndDeleteOwner)
        .pause(1);

    /**
     * Browser Only Journey
     * Read-only user just exploring
     */
    public static ChainBuilder browserJourney = 
        exec(session -> session.set("journeyType", "Browser"))
        .exec(BrowseScenario.browsePetTypes)
        .pause(3, 5)
        .exec(BrowseScenario.browseVets)
        .pause(2, 4)
        .exec(SearchScenario.viewVetDetails)
        .pause(3, 6)
        .exec(BrowseScenario.browseOwners)
        .pause(2);

    /**
     * Emergency Visit Journey
     * User quickly schedules urgent visit
     */
    public static ChainBuilder emergencyVisitJourney = 
        exec(session -> session.set("journeyType", "Emergency"))
        // Fast search for existing owner
        .exec(SearchScenario.searchOwnerByName)
        .pause(1) // Minimal delay - urgent!
        // Quickly schedule visit
        .exec(OwnerPetScenario.createOwnerAndPet)
        .pause(1)
        .exec(VisitScenario.createVisit);
}
