package example.simulations.performance.LoadTest;

import example.config.Config;
import example.scenarios.AdminFlows;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;

public class ClinicalLoadTest extends Simulation {
    {
        setUp(
                // --- NHÓM NGƯỜI DÙNG CHÍNH (70% Traffic) ---
                // Khám thường & Đăng ký mới
                ClinicalFlows.newPatientRegistration.injectOpen(
                        rampUsers(50).during(60),            // Warm up 50 users trong 1 phút
                        constantUsersPerSec(5.0).during(300)  // 5 users/giây x 300s = 1500 users
                ),

                // --- NHÓM NGƯỜI DÙNG CÓ NHIỀU PET (20% Traffic) ---
                // Mô phỏng gia đình nuôi nhiều thú cưng
                ClinicalFlows.multiPetOwnerJourney.injectOpen(
                        nothingFor(30),                      // Bắt đầu sau warm up
                        rampUsers(20).during(30),            // Warm up 20 users
                        constantUsersPerSec(1.5).during(300) // 1.5 users/giây = 450 users
                ),

                // --- NHÓM NGƯỜI DÙNG PHỤ (8% Traffic) ---
                // Tìm kiếm, Đổi lịch
                ClinicalFlows.searchOwner.injectOpen(
                        constantUsersPerSec(0.8).during(300)  // 240 users
                ),
                ClinicalFlows.rescheduleVisit.injectOpen(
                        constantUsersPerSec(0.5).during(300)  // 150 users
                ),

                // --- NHÓM ĐỘT BIẾN (2% Traffic) ---
                // Cấp cứu & Admin
                ClinicalFlows.emergencyVisit.injectOpen(
                        nothingFor(20),                      // Vào sau 20s
                        rampUsers(50).during(280)            // 50 users trong 280s
                ),
                AdminFlows.onboardVet.injectOpen(
                        nothingFor(10),
                        rampUsers(5).during(290)             // 5 admin users
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