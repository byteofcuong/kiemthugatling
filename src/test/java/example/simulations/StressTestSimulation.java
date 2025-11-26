package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.OwnerPetScenario;
import io.gatling.javaapi.core.*;
import java.time.Duration;

/**
 * STRESS TEST - Find the breaking point
 * 
 * Strategy: Gradually increase load to find system limits
 * - Ramp up from 0 to 2000 users over 2 minutes
 * - Purpose: Identify at what point the system fails or degrades
 * - Success criteria: Find max concurrent users before errors appear
 * 
 * Expected outcomes:
 * - System should handle at least 500 concurrent users
 * - Response time should stay under 2s for 95% of requests
 * - Error rate should stay below 5% even under max load
 */
public class StressTestSimulation extends BaseSimulation {

    private static final ScenarioBuilder stressScenario = scenario("Stress Test - Find Breaking Point")
        .exec(OwnerPetScenario.createOwnerAndPet);

    {
        setUp(
            // Gradually increase from 0 to 2000 users over 120 seconds
            stressScenario.injectOpen(
                rampUsers(2000).during(Duration.ofMinutes(2))
            )
        )
        .protocols(httpProtocol)
        .assertions(
            // Allow up to 5% error rate during stress test
            global().failedRequests().percent().lt(5.0),
            // 95th percentile should stay under 2 seconds
            global().responseTime().percentile(95).lt(2000)
        );
    }
}
