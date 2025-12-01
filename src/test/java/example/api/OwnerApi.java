package example.api;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import example.config.Constants;
import io.gatling.javaapi.core.*;

public class OwnerApi {

    /**
     * LẤY DANH SÁCH CHỦ SỞ HỮU
     */
    public static ChainBuilder getAllOwners =
            exec(
                    http("Get All Owners")
                            .get(Constants.OWNERS_API)
                            .check(status().is(200))
                            .check(jsonPath("$[*].id").findAll().saveAs("allOwnerIds"))
            );

    /**
     * TẠO CHỦ SỞ HỮU MỚI
     */
    public static ChainBuilder createOwner =
            exec(
                    http("Create Owner")
                            .post(Constants.OWNERS_API)
                            .body(StringBody("{" +
                                    "\"firstName\": \"#{firstName}\"," +
                                    "\"lastName\": \"#{lastName}\"," +
                                    "\"address\": \"#{address}\"," +
                                    "\"city\": \"#{city}\"," +
                                    "\"telephone\": \"#{telephone}\"" +
                                    "}"))
                            .check(status().is(201))
                            .check(jsonPath("$.id").saveAs(Constants.OWNER_ID))
                            .check(jsonPath("$.firstName").is(session -> session.getString("firstName")))
            );

    /**
     * LẤY THÔNG TIN CHỦ SỞ HỮU THEO ID
     */
    public static ChainBuilder getOwnerById =
            exec(
                    http("Get Owner by ID: #{" + Constants.OWNER_ID + "}")
                            .get(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                            .check(status().is(200))
                            .check(jsonPath("$.id").exists())
            );


    /**
     * CẬP NHẬT THÔNG TIN CHỦ SỞ HỮU
     */
    public static ChainBuilder updateOwner =
            exec(
                    http("Update Owner #{" + Constants.OWNER_ID + "}")
                            .put(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                            .body(StringBody("{" +
                                    "\"id\": #{" + Constants.OWNER_ID + "}," +
                                    "\"firstName\": \"#{firstName}\"," +
                                    "\"lastName\": \"#{lastName}\"," +
                                    "\"address\": \"#{address} - UPDATED\"," +
                                    "\"city\": \"#{city}\"," +
                                    "\"telephone\": \"#{telephone}\"" +
                                    "}"))
                            .check(status().is(204))
            );

    /**
     * XÓA CHỦ SỞ HỮU THEO ID
     */
    public static ChainBuilder deleteOwner =
            exec(
                    http("Delete Owner #{" + Constants.OWNER_ID + "}")
                            .delete(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}")
                            .check(status().is(204))
            );

    /**
     * TẠO CHỦ SỞ HỮU VỚI DỮ LIỆU KHÔNG HỢP LỆ (THIẾU firstName)
     */
    public static ChainBuilder createOwnerInvalidData =
            exec(
                    http("Create Owner - Empty First Name (Invalid)")
                            .post(Constants.OWNERS_API)
                            .body(StringBody("{" +
                                    "\"firstName\": \"\"," +
                                    "\"lastName\": \"TestLast\"," +
                                    "\"address\": \"123 Test St\"," +
                                    "\"city\": \"TestCity\"," +
                                    "\"telephone\": \"1234567890\"" +
                                    "}"))
                            .check(status().in(400, 422))
            );


    /**
     * TÌM KIẾM CHỦ SỞ HỮU THEO LAST NAME
     */
    public static ChainBuilder searchOwnerByLastName =
            exec(
                    http("Search Owner by Last Name: #{lastName}")
                            .get(Constants.OWNERS_API)
                            .queryParam("lastName", "#{lastName}")
                            .check(status().is(200))
            );
}