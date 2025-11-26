package example.api;

import io.gatling.javaapi.core.ChainBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VetApi {
    public static ChainBuilder list = exec(
            http("List Vets")
                    .get("/vets") //
                    .check(status().is(200))
    );
}
