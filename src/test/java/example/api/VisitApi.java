package example.api;

import io.gatling.javaapi.core.ChainBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VisitApi {

    public static ChainBuilder create = exec(
            http("Create Visit")
                    // Dùng ownerId và petId (lưu từ bước trước)
                    .post("/owners/#{ownerId}/pets/#{petId}/visits")
                    .body(ElFileBody("bodies/visit.json")).asJson()
                    .check(status().is(201)) // Cehck trả về 201
    );
}