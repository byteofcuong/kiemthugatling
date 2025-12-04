package example.api;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import example.config.Constants;
import io.gatling.javaapi.core.*;

public class VetApi {

    /**
     * LẤY TẤT CẢ VETS
     */
    public static ChainBuilder getAllVets =
            exec(
                    http("Get All Vets")
                            .get(Constants.VETS_API)
                            .check(status().is(200))
            );

    /**
     * TẠO MỚI VET
     */
    public static ChainBuilder createVet =
            exec(
                    http("Create Vet")
                            .post(Constants.VETS_API)
                            .body(StringBody("{" +
                                    "\"firstName\": \"#{firstName}\"," +
                                    "\"lastName\": \"#{lastName}\"," +
                                    "\"specialties\": []" +
                                    "}"))
                            .check(status().is(201))
                            .check(jsonPath("$.id").saveAs(Constants.VET_ID))
            );

    /**
     * GÁN CHUYÊN KHOA CHO VET
     */
    public static ChainBuilder assignSpecialtyToVet =
            exec(
                    http("Assign Specialty to Vet #{" + Constants.VET_ID + "}")
                            .put(Constants.VETS_API + "/#{" + Constants.VET_ID + "}")
                            .body(StringBody("{" +
                                    "\"id\": #{" + Constants.VET_ID + "}," +
                                    "\"firstName\": \"#{firstName}\"," +
                                    "\"lastName\": \"#{lastName}\"," +
                                    "\"specialties\": [{" +
                                    "\"id\": #{specialtyIds}," +
                                    "\"name\": \"#{specialtyName}\"" +
                                    "}]" +
                                    "}"))
                            .check(status().is(204))
            );

    /**
     * CẬP NHẬT VET
     */
    public static ChainBuilder updateVet =
            exec(
                    http("Update Vet #{" + Constants.VET_ID + "}")
                            .put(Constants.VETS_API + "/#{" + Constants.VET_ID + "}")
                            .body(StringBody("{" +
                                    "\"id\": #{" + Constants.VET_ID + "}," +
                                    "\"firstName\": \"#{firstName}Updated\"," +
                                    "\"lastName\": \"#{lastName}\"," +
                                    "\"specialties\": []" +
                                    "}"))
                            .check(status().is(204))
            );

    /**
     * XÓA VET
     */
    public static ChainBuilder deleteVet =
            exec(
                    http("Delete Vet #{" + Constants.VET_ID + "}")
                            .delete(Constants.VETS_API + "/#{" + Constants.VET_ID + "}")
                            .check(status().is(204))
            );

    /**
     * LẤY TẤT CẢ CHUYÊN KHOA
     */
    public static ChainBuilder getAllSpecialties =
            exec(
                    http("Get All Specialties")
                            .get("/api/specialties")
                            .check(status().is(200))
            );

    /**
     * TẠO MỚI CHUYÊN KHOA
     */
    public static ChainBuilder createSpecialty =
            exec(session -> session.set("newSpecialtyName", "Spec" + System.currentTimeMillis()))
                    .exec(
                            http("Create Specialty (Admin)")
                                    .post("/api/specialties")
                                    .body(StringBody("{\"name\": \"#{newSpecialtyName}\"}"))
                                    .check(status().is(201))
                                    .check(jsonPath("$.id").saveAs("newSpecialtyId"))
                    );

    /**
     * CẬP NHẬT CHUYÊN KHOA
     */
    public static ChainBuilder updateSpecialty =
            exec(
                    http("Update Specialty (Admin)")
                            .put("/api/specialties/#{newSpecialtyId}")
                            .body(StringBody("{" +
                                    "\"id\": #{newSpecialtyId}," +
                                    "\"name\": \"#{newSpecialtyName} - Updated\"" +
                                    "}"))
                            .check(status().is(204))
            );

    /**
     * XÓA CHUYÊN KHOA
     */
    public static ChainBuilder deleteSpecialty =
            exec(
                    http("Delete Specialty (Admin)")
                            .delete("/api/specialties/#{newSpecialtyId}")
                            .check(status().is(204))
            );
}