package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.OwnerPetScenario;
import io.gatling.javaapi.core.*;

/**
 * BASELINE PERFORMANCE TEST
 * 
 * Strategy: Standard load test with quality gates
 * - 5 concurrent users (default, configurable via -Dvu=N)
 * - Purpose: Establish baseline performance metrics
 * - Success criteria: Must pass all quality gates
 * 
 * Quality Gates (CI/CD Ready):
 * - 95% of requests must complete within 500ms
 * - Error rate must be below 1%
 * - Mean response time should be under 300ms
 * 
 * If any assertion fails, build will fail in CI/CD pipeline
 */
public class PetClinicSimulation extends BaseSimulation {

    // Load VU count from system properties (default: 5 users)
    private static final int vu = Integer.getInteger("vu", 5);

    // Define scenario using the OwnerPetScenario chain
    private static final ScenarioBuilder scenario = scenario("PetClinic Owner and Pet Creation")
        .exec(OwnerPetScenario.createOwnerAndPet);

    // Setup injection profile and execute the test with Quality Gates
    {
        setUp(
            scenario.injectOpen(
                atOnceUsers(vu)
            )
        )
        .protocols(httpProtocol)
        // QUALITY GATES - These will cause build failure if not met
        .assertions(
            // KPI 1: 95% of requests must complete within 500ms
            global().responseTime().percentile(95).lt(500),
            
            // KPI 2: Error rate must be below 1%
            global().failedRequests().percent().lt(1.0),
            
            // KPI 3: Mean response time should be fast
            global().responseTime().mean().lt(300),
            
            // KPI 4: All requests should succeed in normal load
            global().successfulRequests().percent().gt(99.0)
        );
    }
}
