package example.simulations.functional;

import example.config.Config;
import example.scenarios.AdminFlows;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;

public class SmokeTest extends Simulation {
    {
        setUp(
                // 1. Kiểm tra các luồng Khám bệnh chính
                ClinicalFlows.newPatientRegistration.injectOpen(atOnceUsers(1)),
                ClinicalFlows.existingOwnerAddPet.injectOpen(atOnceUsers(1)),
                ClinicalFlows.rescheduleVisit.injectOpen(atOnceUsers(1)),
                ClinicalFlows.emergencyVisit.injectOpen(atOnceUsers(1)),
                ClinicalFlows.walkInVisit.injectOpen(atOnceUsers(1)),
                ClinicalFlows.searchOwner.injectOpen(atOnceUsers(1)),
                ClinicalFlows.completeLifecycle.injectOpen(atOnceUsers(1)),


                // 2. Kiểm tra các luồng Admin
                AdminFlows.onboardVet.injectOpen(atOnceUsers(1)),
                AdminFlows.expandPetTypes.injectOpen(atOnceUsers(1)),
                AdminFlows.expandSpecialties.injectOpen(atOnceUsers(1))
        )
                .protocols(Config.httpProtocol)
                // QUAN TRỌNG: Không chấp nhận bất kỳ lỗi nào (Zero Tolerance)
                .assertions(global().failedRequests().count().is(0L));
    }
}