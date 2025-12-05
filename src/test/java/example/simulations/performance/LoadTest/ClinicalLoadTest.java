package example.simulations.performance.LoadTest;

import example.config.Config;
import example.scenarios.AdminFlows;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;

import java.time.Duration;

import static io.gatling.javaapi.core.CoreDsl.*;

public class ClinicalLoadTest extends Simulation {
    private static final Duration RAMP_DURATION = Duration.ofMinutes(5);
    private static final Duration STEADY_DURATION = Duration.ofMinutes(15);
    {
        setUp(
                ClinicalFlows.newPatientRegistration.injectClosed(
                        rampConcurrentUsers(0).to(15).during(RAMP_DURATION),
                        constantConcurrentUsers(15).during(STEADY_DURATION)
                ),

                ClinicalFlows.multiPetOwnerJourney.injectClosed(
                        rampConcurrentUsers(0).to(5).during(RAMP_DURATION),
                        constantConcurrentUsers(5).during(STEADY_DURATION)
                ),

                ClinicalFlows.searchOwner.injectClosed(
                        rampConcurrentUsers(0).to(3).during(RAMP_DURATION),
                        constantConcurrentUsers(3).during(STEADY_DURATION)
                ),
                ClinicalFlows.rescheduleVisit.injectClosed(
                        constantConcurrentUsers(1).during(STEADY_DURATION.plus(RAMP_DURATION))
                ),

                ClinicalFlows.emergencyVisit.injectClosed(
                        constantConcurrentUsers(1).during(STEADY_DURATION.plus(RAMP_DURATION))
                ),
                AdminFlows.onboardVet.injectClosed(
                        constantConcurrentUsers(1).during(STEADY_DURATION.plus(RAMP_DURATION))
                )
        )
                .protocols(Config.httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lt(1000),
                        global().responseTime().mean().lt(300),
                        global().failedRequests().percent().lt(1.0)
                );
    }
}