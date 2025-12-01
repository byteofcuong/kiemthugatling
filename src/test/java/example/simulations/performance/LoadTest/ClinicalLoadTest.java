package example.simulations.performance.LoadTest;

import example.config.Config;
import example.scenarios.AdminFlows;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;

public class ClinicalLoadTest extends Simulation {
    {
        setUp(
                // --- NHÓM NGƯỜI DÙNG CHÍNH (80% Traffic) ---
                // Khám thường & Đăng ký mới
                ClinicalFlows.newPatientRegistration.injectOpen(
                        rampUsers(10).during(30),           // Warm up
                        constantUsersPerSec(2).during(300)  // Chạy ổn định 5 phút
                ),

                // --- NHÓM NGƯỜI DÙNG PHỤ (15% Traffic) ---
                // Tìm kiếm, Đổi lịch
                ClinicalFlows.searchOwner.injectOpen(
                        constantUsersPerSec(1).during(300)
                ),
                ClinicalFlows.rescheduleVisit.injectOpen(
                        constantUsersPerSec(0.5).during(300)
                ),

                // --- NHÓM ĐỘT BIẾN (5% Traffic) ---
                // Cấp cứu & Admin
                ClinicalFlows.emergencyVisit.injectOpen(
                        nothingFor(20),                     // Vào sau 20s
                        rampUsers(5).during(300)
                ),
                AdminFlows.onboardVet.injectOpen(
                        nothingFor(10),
                        atOnceUsers(1)                      // Admin thỉnh thoảng mới vào
                )
        )
                .protocols(Config.httpProtocol)
                // TIÊU CHÍ CHẤP NHẬN (SLA)
                .assertions(
                        global().responseTime().percentile3().lt(800), // 95% request < 800ms
                        global().failedRequests().percent().lt(1.0)    // Lỗi < 1%
                );
    }
}