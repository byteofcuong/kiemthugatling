package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;
import java.time.Duration;

/**
 * SPIKE TEST - Sudden traffic surge
 * 
 * Simulates unexpected traffic spikes:
 * - Normal load: 10 users
 * - Sudden spike: 200 users in 10 seconds
 * - Back to normal: 10 users
 * 
 * Tests:
 * - Auto-scaling capabilities
 * - Circuit breakers
 * - Queue management
 * - Graceful degradation
 * 
 * Real scenarios:
 * - Marketing campaign launch
 * - News coverage
 * - Social media viral event
 */
public class SpikeTestSimulation extends BaseSimulation {

    private static final ScenarioBuilder spikeScenario = scenario("Spike Test - Sudden Load")
        .exec(UserJourneyScenario.newPetOwnerJourney);

    {
        setUp(
            spikeScenario.injectOpen(
                // Normal load
                constantUsersPerSec(2).during(Duration.ofSeconds(30)),
                // Sudden spike!
                rampUsers(200).during(Duration.ofSeconds(10)),
                // Cool down period
                constantUsersPerSec(2).during(Duration.ofSeconds(30))
            )
        )
        .protocols(httpProtocol)
        .assertions(
            // Allow higher error rate during spike
            global().failedRequests().percent().lt(10.0),
            // Response time can degrade but not catastrophically
            global().responseTime().percentile(95).lt(3000)
        );
    }
}
