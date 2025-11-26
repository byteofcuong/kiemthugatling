package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * READ-HEAVY Simulation - Browse and search operations
 * 
 * Simulates typical usage where users mostly read data:
 * - Browse lists
 * - Search for specific records
 * - View details
 * 
 * This is the most common pattern in production systems.
 * Tests database read performance and caching effectiveness.
 */
public class ReadHeavySimulation extends BaseSimulation {

    private static final int vu = Integer.getInteger("vu", 50);

    private static final ScenarioBuilder readScenario = scenario("Read-Heavy User Behavior")
        .exec(BrowseScenario.fullBrowse)
        .pause(2, 4)
        .exec(SearchScenario.searchAndViewOwnerDetails)
        .pause(2, 3)
        .exec(SearchScenario.viewVetDetails)
        .pause(1, 2)
        .exec(VisitScenario.viewAllVisits);

    {
        setUp(
            readScenario.injectOpen(atOnceUsers(vu))
        )
        .protocols(httpProtocol)
        .assertions(
            // Read operations should be very fast
            global().responseTime().percentile(95).lt(300),
            global().failedRequests().count().is(0L)
        );
    }
}
