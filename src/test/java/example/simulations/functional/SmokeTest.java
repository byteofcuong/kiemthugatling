package example.simulations.functional;

import example.config.Config;
import example.scenarios.AdminFlows;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;

public class SmokeTest extends Simulation {
    {
        setUp(
                ClinicalFlows.newPatientRegistration.injectOpen(atOnceUsers(1)),
                ClinicalFlows.existingOwnerAddPet.injectOpen(atOnceUsers(1)),
                ClinicalFlows.rescheduleVisit.injectOpen(atOnceUsers(1)),
                ClinicalFlows.emergencyVisit.injectOpen(atOnceUsers(1)),
                ClinicalFlows.walkInVisit.injectOpen(atOnceUsers(1)),
                ClinicalFlows.searchOwner.injectOpen(atOnceUsers(1)),
                ClinicalFlows.completeLifecycle.injectOpen(atOnceUsers(1)),


                AdminFlows.onboardVet.injectOpen(atOnceUsers(1)),
                AdminFlows.expandPetTypes.injectOpen(atOnceUsers(1)),
                AdminFlows.expandSpecialties.injectOpen(atOnceUsers(1))
        )
                .protocols(Config.httpProtocol)
                .assertions(global().failedRequests().count().is(0L));
    }
}