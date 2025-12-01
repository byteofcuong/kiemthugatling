package example.api;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import example.config.Constants;
import io.gatling.javaapi.core.*;

public class PetApi {

    /**
     * TẠO PET
     */
    public static ChainBuilder createPetForOwner =
            exec(
                    http("Create Pet for Owner #{" + Constants.OWNER_ID + "}")
                            .post(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}/pets")
                            .body(StringBody("{" +
                                    "\"name\": \"#{petName}\"," +        // <--- SỬA THÀNH petName
                                    "\"birthDate\": \"#{birthDate}\"," +
                                    "\"type\": {" +
                                    "\"id\": #{typeId}," +
                                    "\"name\": \"#{typeName}\"" +    // <--- SỬA THÀNH typeName
                                    "}" +
                                    "}"))
                            .check(status().is(201))
                            .check(jsonPath("$.id").saveAs(Constants.PET_ID))
                            .check(jsonPath("$.name").is(session -> session.getString("petName")))
            );

    /**
     * LẤY THÔNG TIN PET THEO ID
     */
    public static ChainBuilder getPetById =
            exec(
                    http("Get Pet by ID: #{" + Constants.PET_ID + "}")
                            .get(Constants.PETS_API + "/#{" + Constants.PET_ID + "}")
                            .check(status().is(200))
                            .check(jsonPath("$.id").exists())
            );

    /**
     * CẬP NHẬT THÔNG TIN PET
     */
    public static ChainBuilder updatePet =
            exec(
                    http("Update Pet #{" + Constants.PET_ID + "}")
                            .put(Constants.PETS_API + "/#{" + Constants.PET_ID + "}")
                            .body(StringBody("{" +
                                    "\"id\": #{" + Constants.PET_ID + "}," +
                                    "\"name\": \"#{petName} - Updated\"," + // <--- SỬA THÀNH petName
                                    "\"birthDate\": \"#{birthDate}\"," +
                                    "\"type\": {" +
                                    "\"id\": #{typeId}," +
                                    "\"name\": \"#{typeName}\"" +
                                    "}" +
                                    "}"))
                            .check(status().is(204))
            );

    /**
     * XÓA PET THEO ID
     */
    public static ChainBuilder deletePet =
            exec(
                    http("Delete Pet #{" + Constants.PET_ID + "}")
                            .delete(Constants.PETS_API + "/#{" + Constants.PET_ID + "}")
                            .check(status().is(204))
            );

    /**
     * QUẢN LÝ LOẠI PET (CHỈ DÀNH CHO ADMIN)
     */
    public static ChainBuilder getAllPetTypes =
            exec(
                    http("Get All Pet Types")
                            .get("/api/pettypes")
                            .check(status().is(200))
            );

    /**
     * TẠO, CẬP NHẬT, XÓA LOẠI PET (CHỈ DÀNH CHO ADMIN)
     */
    public static ChainBuilder createPetType =
            exec(session -> session.set("newPetTypeName", "NewType" + System.currentTimeMillis()))
                    .exec(
                            http("Create Pet Type (Admin)")
                                    .post("/api/pettypes")
                                    .body(StringBody("{\"name\": \"#{newPetTypeName}\"}"))
                                    .check(status().is(201))
                                    .check(jsonPath("$.id").saveAs("newPetTypeId"))
                    );

    /**
     * CẬP NHẬT LOẠI PET
     */
    public static ChainBuilder updatePetType =
            exec(
                    http("Update Pet Type (Admin)")
                            .put("/api/pettypes/#{newPetTypeId}")
                            .body(StringBody("{" +
                                    "\"id\": #{newPetTypeId}," +
                                    "\"name\": \"#{newPetTypeName} - Updated\"" +
                                    "}"))
                            .check(status().is(204))
            );

    /**
     * XÓA LOẠI PET
     */
    public static ChainBuilder deletePetType =
            exec(
                    http("Delete Pet Type (Admin)")
                            .delete("/api/pettypes/#{newPetTypeId}")
                            .check(status().is(204))
            );
}