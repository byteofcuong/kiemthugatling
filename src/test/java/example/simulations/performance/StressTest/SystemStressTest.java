package example.simulations.performance.StressTest;

import example.config.Config;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;

public class SystemStressTest extends Simulation {
    {
        setUp(
                ClinicalFlows.completeLifecycle.injectOpen(
                        incrementUsersPerSec(5)
                                .times(10)
                                .eachLevelLasting(60)
                                .startingFrom(5)
                )
        )
                .protocols(Config.httpProtocol)
                .maxDuration(30 * 60);
    }
}