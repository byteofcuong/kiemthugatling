package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;
import java.time.Duration;

/**
 * FULL CRUD TEST - Complete data lifecycle
 * 
 * Tests all database operations in sequence:
 * - CREATE: Add new records
 * - READ: Query and search
 * - UPDATE: Modify existing data
 * - DELETE: Remove records
 * 
 * Validates:
 * - Data consistency
 * - Transaction integrity
 * - Referential integrity (cascading deletes)
 * - Concurrent access handling
 */
public class FullCRUDSimulation extends BaseSimulation {

    private static final int vu = Integer.getInteger("vu", 20);

    private static final ScenarioBuilder crudScenario = scenario("Full CRUD Lifecycle")
        .exec(DeleteScenario.fullCRUDCycle);

    {
        setUp(
            crudScenario.injectOpen(
                rampUsers(vu).during(Duration.ofSeconds(20))
            )
        )
        .protocols(httpProtocol)
        .assertions(
            // All operations should succeed
            global().failedRequests().count().is(0L),
            // CRUD operations combined
            global().responseTime().percentile(95).lt(600),
            // Verify each operation type
            details("Create Owner").successfulRequests().percent().is(100.0),
            details("Read Owner.*").successfulRequests().percent().is(100.0),
            details("Update Owner.*").successfulRequests().percent().is(100.0),
            details("Delete Owner.*").successfulRequests().percent().is(100.0)
        );
    }
}
