package example.api;

import io.gatling.javaapi.core.ChainBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class PetApi {

    public static ChainBuilder create = exec(
            http("Create Pet")
                    // Dùng ownerId (lưu từ bước trước)
                    .post("/owners/#{ownerId}/pets")
                    .body(ElFileBody("bodies/pet.json")).asJson()
                    .check(status().is(201)) // Check trả về 201
                    // Lưu ID pet dùng cho bước sau
                    .check(jsonPath("$.id").saveAs("petId"))
    );
}