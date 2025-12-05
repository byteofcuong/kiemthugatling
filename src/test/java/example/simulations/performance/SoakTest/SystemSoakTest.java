package example.simulations.performance.SoakTest;

import example.config.Config;
import example.scenarios.AdminFlows;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;
import java.time.Duration;

public class SystemSoakTest extends Simulation {
    {
        setUp(
                // LUỒNG GHI
                ClinicalFlows.completeLifecycle.injectClosed(
                        rampConcurrentUsers(1).to(15).during(Duration.ofMinutes(2)),
                        constantConcurrentUsers(15).during(Duration.ofMinutes(20)),
                        rampConcurrentUsers(15).to(5).during(Duration.ofMinutes(3))
                ),

                // LUỒNG ĐỌC
                ClinicalFlows.searchOwner.injectClosed(
                        rampConcurrentUsers(1).to(20).during(Duration.ofMinutes(2)),
                        constantConcurrentUsers(20).during(Duration.ofMinutes(20)),
                        rampConcurrentUsers(20).to(5).during(Duration.ofMinutes(3))
                ),

                // LUỒNG ADMIN
                AdminFlows.onboardVet.injectClosed(
                        constantConcurrentUsers(1).during(Duration.ofMinutes(25))
                )
        )
                .protocols(Config.httpProtocol)
                .assertions(
                        global().failedRequests().percent().lt(1.0),
                        global().responseTime().percentile3().lt(500),
                        global().responseTime().mean().lt(300)
                )
                .maxDuration(Duration.ofMinutes(35));
    }
}