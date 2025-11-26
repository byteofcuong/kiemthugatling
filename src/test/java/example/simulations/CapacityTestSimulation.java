package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;
import java.time.Duration;

/**
 * CAPACITY TEST - Maximum sustained throughput
 * 
 * Goal: Find the maximum number of users the system can handle
 * while maintaining acceptable performance
 * 
 * Strategy:
 * - Start with 50 users
 * - Increase by 50 every minute
 * - Continue until hitting performance threshold
 * 
 * Success criteria:
 * - Response time stays under 1s
 * - Error rate below 5%
 * 
 * Use results to:
 * - Set capacity planning targets
 * - Determine infrastructure needs
 * - Establish scaling triggers
 */
public class CapacityTestSimulation extends BaseSimulation {

    private static final ScenarioBuilder capacityScenario = scenario("Capacity Test")
        .exec(UserJourneyScenario.newPetOwnerJourney);

    {
        setUp(
            capacityScenario.injectOpen(
                incrementUsersPerSec(10)
                    .times(10)
                    .eachLevelLasting(Duration.ofSeconds(30))
                    .separatedByRampsLasting(Duration.ofSeconds(10))
                    .startingFrom(10)
            )
        )
        .protocols(httpProtocol)
        .maxDuration(Duration.ofMinutes(10))
        .assertions(
            global().responseTime().percentile(95).lt(1000),
            global().failedRequests().percent().lt(5.0)
        );
    }
}
