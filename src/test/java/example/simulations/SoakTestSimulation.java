package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.OwnerPetScenario;
import io.gatling.javaapi.core.*;
import java.time.Duration;

/**
 * SOAK TEST - Test system endurance
 * 
 * Strategy: Maintain constant moderate load for extended period
 * - Keep 50 users active for 30 minutes
 * - Purpose: Detect memory leaks, resource exhaustion, performance degradation
 * - Success criteria: Performance remains stable throughout entire duration
 * 
 * What to monitor:
 * - Memory usage (should not continuously increase)
 * - Response time (should remain consistent)
 * - Database connections (should not leak)
 * - CPU usage (should stabilize)
 * 
 * Expected outcomes:
 * - No memory leaks
 * - Response time variance < 20%
 * - Zero errors
 * - Stable resource consumption
 */
public class SoakTestSimulation extends BaseSimulation {

    private static final ScenarioBuilder soakScenario = scenario("Soak Test - 30min Endurance")
        .exec(OwnerPetScenario.createOwnerAndPet);

    {
        setUp(
            // Maintain constant 50 users for 30 minutes
            // Each user completes scenario then immediately starts again
            soakScenario.injectOpen(
                constantUsersPerSec(50).during(Duration.ofMinutes(30))
            )
        )
        .protocols(httpProtocol)
        .assertions(
            // Strict quality gates for soak test
            global().failedRequests().percent().is(0.0),
            // Response time should be consistently fast
            global().responseTime().percentile(95).lt(500),
            // Mean response time should stay low
            global().responseTime().mean().lt(300)
        );
    }
}
