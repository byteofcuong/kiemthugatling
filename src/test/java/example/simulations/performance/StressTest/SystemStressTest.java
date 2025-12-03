package example.simulations.performance.StressTest;

import example.config.Config;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;

public class SystemStressTest extends Simulation {
    {
        setUp(
                ClinicalFlows.completeLifecycle.injectOpen(
                        // Bắt đầu từ 1 user/giây, tăng dần lên
                        incrementUsersPerSec(3)      // Tăng nhanh hơn để giảm total users
                                .times(5)                // Giảm xuống 5 bậc (từ 10) -> Max 16 user/s
                                .eachLevelLasting(20)    // Giảm xuống 20s (từ 30s)
                                .startingFrom(1)         // Bắt đầu từ 1
                )
        ).protocols(Config.httpProtocol);

    }
}