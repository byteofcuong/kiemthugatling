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
                                .times(20)
                                .eachLevelLasting(45)
                                .startingFrom(30)
                )
        )
                .protocols(Config.httpProtocol)
                .maxDuration(30 * 60);
    }
}