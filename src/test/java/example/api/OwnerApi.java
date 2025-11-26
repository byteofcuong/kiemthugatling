package example.api;

import io.gatling.javaapi.core.ChainBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class OwnerApi {

    public static ChainBuilder create = exec(
            http("Create Owner") // Tên trên báo cáo
                    .post("/owners") // Endpoint (Gatling tự nối với BaseUrl)
                    .body(ElFileBody("bodies/owner.json")).asJson() // Dùng file JSON mẫu
                    .check(status().is(201)) // Check trả về 201 Created
                    .check(jsonPath("$.id").saveAs("ownerId")) // Lưu ID dùng cho bước sau
    );

    public static ChainBuilder update = exec(
            http("Update Owner")
                    .put("/owners/#{ownerId}") // Endpoint PUT /owners/{id}
                    .body(StringBody(
                            "{" +
                                    "\"firstName\": \"#{firstName}\"," +
                                    "\"lastName\": \"#{lastName}\"," +
                                    "\"address\": \"UPDATED ADDRESS " + System.currentTimeMillis() + "\"," + // Sửa địa chỉ
                                    "\"city\": \"Ho Chi Minh\"," +
                                    "\"telephone\": \"#{telephone}\"" +
                                    "}"
                    )).asJson()
                    .check(status().is(200)) // Check trả về 200 OK
                    .check(jsonPath("$.address").saveAs("updatedAddress")) // Lưu lại để verify nếu cần
    );

    public static ChainBuilder search = exec(
            http("Search Owner")
                    .get("/owners")
                    .queryParam("lastName", "#{createdLastName}") // Tìm theo tên vừa tạo
                    .check(status().is(200))
                    // Check kết quả trả về phải là mảng (jsonPath $[0].id tồn tại)
                    .check(jsonPath("$[0].id").exists())
    );
}
