package example.config;

import io.gatling.javaapi.http.HttpProtocolBuilder;
import static io.gatling.javaapi.http.HttpDsl.*;

public class Config {
    public static int USERS = Integer.getInteger("users", 10);

    public static HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:9966/petclinic")

            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling Performance Test")
            
            // Connection pooling
            .maxConnectionsPerHost(10)
            .shareConnections();
}
