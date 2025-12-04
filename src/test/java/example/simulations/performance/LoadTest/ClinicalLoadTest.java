package example.simulations.performance.LoadTest;

import example.config.Config;
import example.scenarios.AdminFlows;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class ClinicalLoadTest extends Simulation {
    private static final Duration TEST_DURATION = Duration.ofMinutes(20);
    {
        setUp(
                // --- NHÓM NGƯỜI DÙNG CHÍNH (70% Traffic) ---
                // Khám thường & Đăng ký mới
                ClinicalFlows.newPatientRegistration.injectOpen(
                        rampUsers(420).during(TEST_DURATION)
                ),

                // --- NHÓM NGƯỜI DÙNG CÓ NHIỀU PET (20% Traffic) ---
                // Mô phỏng gia đình nuôi nhiều thú cưng
                ClinicalFlows.multiPetOwnerJourney.injectOpen(
                        nothingFor(Duration.ofMinutes(1)),
                        rampUsers(120).during(TEST_DURATION.minusMinutes(1))
                ),

                // --- NHÓM NGƯỜI DÙNG PHỤ (8% Traffic) ---
                // Tìm kiếm, Đổi lịch
                ClinicalFlows.searchOwner.injectOpen(
                        nothingFor(Duration.ofMinutes(2)),
                        rampUsers(50).during(TEST_DURATION.minusMinutes(2))
                ),
                ClinicalFlows.rescheduleVisit.injectOpen(
                        nothingFor(Duration.ofMinutes(2)),
                        rampUsers(50).during(TEST_DURATION.minusMinutes(2))
                ),

                // --- NHÓM ĐỘT BIẾN (2% Traffic) ---
                // Cấp cứu & Admin
                ClinicalFlows.emergencyVisit.injectOpen(
                        nothingFor(Duration.ofMinutes(2)),
                        rampUsers(50).during(TEST_DURATION.minusMinutes(2))
                ),
                AdminFlows.onboardVet.injectOpen(
                        nothingFor(Duration.ofMinutes(2)),
                        rampUsers(50).during(TEST_DURATION.minusMinutes(2))
                )
        )
                .protocols(Config.httpProtocol)
                // TIÊU CHÍ CHẤP NHẬN (SLA) - Relaxed for initial testing
                .assertions(
                        global().responseTime().percentile3().lt(800),
                        global().responseTime().mean().lt(300),
                        global().failedRequests().percent().lt(1.0)
                );
    }
}