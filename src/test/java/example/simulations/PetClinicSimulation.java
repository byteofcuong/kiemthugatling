package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.OwnerPetScenario;
import io.gatling.javaapi.core.*;

public class PetClinicSimulation extends BaseSimulation {

    // Load VU count from system properties (default: 5 users)
    private static final int vu = Integer.getInteger("vu", 5);

    // Define scenario using the OwnerPetScenario chain
    private static final ScenarioBuilder scenario = scenario("PetClinic Owner and Pet Creation")
        .exec(OwnerPetScenario.createOwnerAndPet);

    // Define assertions
    private static final Assertion assertion = global().failedRequests().count().lt(1L);

    // Setup injection profile and execute the test
    {
        setUp(
            scenario.injectOpen(atOnceUsers(vu))
        )
        .protocols(httpProtocol)
        .assertions(assertion);
    }
}
