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
                // 1. Luồng Ghi (Tạo rác bộ nhớ) - Chạy ổn định
                ClinicalFlows.newPatientRegistration.injectOpen(
                        rampUsersPerSec(1).to(5).during(300), // Warm up 5 phút
                        constantUsersPerSec(5).during(Duration.ofHours(1))  // Chạy 1 giờ
                ),

                // 2. Luồng Đọc (Test Cache Memory) - BỔ SUNG
                // Cache đầy mà không xả -> Tràn RAM
                ClinicalFlows.searchOwner.injectOpen(
                        constantUsersPerSec(10).during(Duration.ofHours(1))  // Chạy 1 giờ
                ),

                // 3. Luồng Admin (Test tác vụ nền) - BỔ SUNG
                // Admin hay gây lock database hoặc leak object lớn
                AdminFlows.onboardVet.injectOpen(
                        // Admin vào làm việc định kỳ (ví dụ: mỗi 5 phút 1 người)
                        constantUsersPerSec(0.003).during(Duration.ofHours(1))  // Chạy 1 giờ
                )
        )
                .protocols(Config.httpProtocol)
                // BỔ SUNG ASSERTION CHO SOAK TEST
                // Chạy lâu mà lỗi > 1% là không đạt (Stability fail)
                .assertions(global().failedRequests().percent().lt(1.0));
    }
}