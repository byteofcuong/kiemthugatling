package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * WRITE-HEAVY Simulation - Create and update operations
 * 
 * Simulates peak registration periods or data migration:
 * - Continuous creation of owners and pets
 * - Update operations
 * - Visit scheduling
 * 
 * Tests:
 * - Database write performance
 * - Transaction handling
 * - Locking mechanisms
 * - Primary key generation
 */
public class WriteHeavySimulation extends BaseSimulation {

    private static final int vu = Integer.getInteger("vu", 30);

    private static final ScenarioBuilder writeScenario = scenario("Write-Heavy Operations")
        .exec(OwnerPetScenario.createOwnerAndPet)
        .pause(1, 2)
        .exec(VisitScenario.createVisit)
        .pause(1, 2)
        .exec(UpdateScenario.updateOwner)
        .pause(1, 2)
        .exec(UpdateScenario.updatePet);

    {
        setUp(
            writeScenario.injectOpen(atOnceUsers(vu))
        )
        .protocols(httpProtocol)
        .assertions(
            // Write operations are slower but should still be reasonable
            global().responseTime().percentile(95).lt(800),
            global().failedRequests().percent().lt(1.0),
            // All creates should succeed
            details("Create Owner").successfulRequests().percent().is(100.0)
        );
    }
}
