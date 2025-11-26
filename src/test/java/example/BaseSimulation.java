package example;

import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

/**
 * Base class for all Gatling simulations
 * Provides common HTTP protocol configuration
 */
public abstract class BaseSimulation extends Simulation {

    // Check for System property 'BASE_URL', if null default to 'http://localhost:9966/petclinic'
    protected static final String BASE_URL = System.getProperty("BASE_URL", "http://localhost:9966/petclinic");

    // Configure HttpProtocolBuilder with common settings
    protected static final HttpProtocolBuilder httpProtocol = http
        .baseUrl(BASE_URL)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")
        .userAgentHeader("Gatling/PerformanceTest");
}
