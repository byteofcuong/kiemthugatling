package example.scenarios;

import example.api.OwnerApi;
import example.api.PetApi;
import example.api.VisitApi;
import example.config.Feeders;
import io.gatling.javaapi.core.ScenarioBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;

public class ClinicalChainScenario {
    public static ScenarioBuilder build() {
        return scenario("Scenario: End-to-End Clinical Visit")
                .feed(Feeders.users)
                .exec(OwnerApi.create)
                .exitHereIfFailed()
                .pause(1)
                .exec(PetApi.create)
                .exitHereIfFailed()
                .pause(1)
                .exec(VisitApi.create);
    }
}
