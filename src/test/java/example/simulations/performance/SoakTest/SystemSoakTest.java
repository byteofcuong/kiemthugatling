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
                // 1. LUỒNG GHI (Write Heavy) - Áp lực lên DB Transaction
                ClinicalFlows.completeLifecycle.injectClosed(
                        rampConcurrentUsers(1).to(15).during(Duration.ofMinutes(2)),
                        constantConcurrentUsers(15).during(Duration.ofMinutes(20)),
                        rampConcurrentUsers(15).to(5).during(Duration.ofMinutes(3))
                ),

                // 2. LUỒNG ĐỌC (Read Heavy) - Áp lực lên Cache & Memory
                ClinicalFlows.searchOwner.injectClosed(
                        rampConcurrentUsers(1).to(20).during(Duration.ofMinutes(2)),
                        constantConcurrentUsers(20).during(Duration.ofMinutes(20)),
                        rampConcurrentUsers(20).to(5).during(Duration.ofMinutes(3))
                ),

                // 3. LUỒNG ADMIN (Background Tasks) - Tìm Leak
                AdminFlows.onboardVet.injectClosed(
                        constantConcurrentUsers(1).during(Duration.ofMinutes(25))
                )
        )
                .protocols(Config.httpProtocol)
                .assertions(
                        // Tiêu chí độ bền:
                        global().failedRequests().percent().lt(1.0),
                        global().responseTime().percentile3().lt(5000),
                        global().responseTime().mean().lt(1000)
                )
                .maxDuration(Duration.ofMinutes(30));
    }
}