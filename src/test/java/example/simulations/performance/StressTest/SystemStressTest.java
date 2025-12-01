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
                        incrementUsersPerSec(2)      // Mức tăng nhẹ nhàng (2 user/s)
                                .times(10)               // Tăng 10 bậc -> Max 20 user/s (Đủ sập app nhỏ)
                                .eachLevelLasting(30)    // Giữ 30s để kịp quan sát
                                .startingFrom(1)         // Bắt đầu từ 1
                )
        ).protocols(Config.httpProtocol);

        // KHÔNG CẦN ASSERTION (Mục tiêu là để nó chết)
    }
}