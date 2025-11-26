package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.BaseSimulation;
import example.Constants;
import io.gatling.javaapi.core.*;

/**
 * Simple smoke test to verify PetClinic API is accessible
 * Tests basic GET endpoints without creating data
 */
public class PetClinicSmokeTest extends BaseSimulation {

    // Simple scenario that just reads data
    private static final ScenarioBuilder smokeTestScenario = scenario("PetClinic Smoke Test")
        .exec(
            http("GET Vets")
                .get(Constants.VETS_API)
                .check(status().is(200))
        )
        .pause(1)
        .exec(
            http("GET Pet Types")
                .get("/api/pettypes")
                .check(status().is(200))
        )
        .pause(1)
        .exec(
            http("GET Owners")
                .get(Constants.OWNERS_API)
                .check(status().is(200))
        );

    // Define assertions - no failures allowed
    private static final Assertion assertion = global().failedRequests().count().is(0L);

    // Run with just 1 user
    {
        setUp(
            smokeTestScenario.injectOpen(atOnceUsers(1))
        )
        .protocols(httpProtocol)
        .assertions(assertion);
    }
}
