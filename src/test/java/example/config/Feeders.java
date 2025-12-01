package example.config;

import io.gatling.javaapi.core.FeederBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;

public class Feeders {
    public static FeederBuilder<String> users = csv("data/users.csv").circular();
    public static FeederBuilder<String> vets = csv("data/vets.csv").random();
}