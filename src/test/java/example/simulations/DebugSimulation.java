package example.simulations;

import example.config.Config;
import example.scenarios.ClinicalChainScenario;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;

public class DebugSimulation extends Simulation {

    {
        setUp(
                ClinicalChainScenario.build()
                        .injectOpen(atOnceUsers(1))
        )
                .protocols(Config.httpProtocol)
                .assertions(
                        global().failedRequests().count().is(0L)
                );
    }
}
