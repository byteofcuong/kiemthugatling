package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;
import java.time.Duration;

/**
 * MIXED WORKLOAD Simulation - Realistic production traffic
 * 
 * Simulates different user types with different behaviors:
 * - 50% Browsers (read-only)
 * - 30% New Pet Owners (create operations)
 * - 15% Returning Users (search & update)
 * - 5% Admin Users (full CRUD)
 * 
 * This pattern reflects real-world usage where most users browse,
 * fewer create data, and very few perform admin operations.
 */
public class MixedWorkloadSimulation extends BaseSimulation {

    private static final int totalUsers = Integer.getInteger("vu", 100);

    // Different user personas with different behaviors
    private static final ScenarioBuilder browserUsers = scenario("Browser Users (50%)")
        .exec(UserJourneyScenario.browserJourney);

    private static final ScenarioBuilder newOwners = scenario("New Pet Owners (30%)")
        .exec(UserJourneyScenario.newPetOwnerJourney);

    private static final ScenarioBuilder returningUsers = scenario("Returning Users (15%)")
        .exec(UserJourneyScenario.returningUserJourney);

    private static final ScenarioBuilder adminUsers = scenario("Admin Users (5%)")
        .exec(UserJourneyScenario.adminUserJourney);

    {
        setUp(
            // 50% browsers - lightweight, read-only
            browserUsers.injectOpen(
                rampUsers((int)(totalUsers * 0.5)).during(Duration.ofSeconds(30))
            ),
            
            // 30% new owners - medium load, creates data
            newOwners.injectOpen(
                rampUsers((int)(totalUsers * 0.3)).during(Duration.ofSeconds(30))
            ),
            
            // 15% returning users - search heavy
            returningUsers.injectOpen(
                rampUsers((int)(totalUsers * 0.15)).during(Duration.ofSeconds(30))
            ),
            
            // 5% admin - intensive CRUD operations
            adminUsers.injectOpen(
                rampUsers((int)(totalUsers * 0.05)).during(Duration.ofSeconds(30))
            )
        )
        .protocols(httpProtocol)
        .assertions(
            global().responseTime().percentile(95).lt(800),
            global().failedRequests().percent().lt(2.0)
        );
    }
}
