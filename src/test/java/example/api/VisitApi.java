package example.api;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.config.Constants;
import io.gatling.javaapi.core.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class VisitApi {

    /**
     * TẠO LỊCH KHÁM CHO THÚ CƯNG
     */
    public static ChainBuilder createVisitForPet = 
        exec(session -> {
            // Generate visit date (today or future)
            String visitDate = LocalDate.now()
                .plusDays((long)(Math.random() * 30))
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
            return session.set("visitDate", visitDate);
        })
        .exec(
            http("Create Visit for Pet #{" + Constants.PET_ID + "}")
                .post("/api/owners/#{" + Constants.OWNER_ID + "}/pets/#{" + Constants.PET_ID + "}/visits")
                .body(StringBody("{" +
                    "\"date\": \"#{visitDate}\"," +
                    "\"description\": \"Regular checkup and vaccination\"" +
                    "}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs(Constants.VISIT_ID))
        );

    /**
     * LẤY DANH SÁCH TẤT CẢ LỊCH KHÁM
     */
    public static ChainBuilder getAllVisits = 
        exec(
            http("Get All Visits")
                .get(Constants.VISITS_API)
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("allVisitIds"))
        );

    /**
     * CẬP NHẬT LỊCH KHÁM
     */
    public static ChainBuilder updateVisit = 
        exec(session -> {
            // Generate new visit date
            String updatedDate = LocalDate.now()
                .plusDays((long)(Math.random() * 60))
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
            return session.set("updatedVisitDate", updatedDate);
        })
        .exec(
            http("Update Visit #{" + Constants.VISIT_ID + "}")
                .put(Constants.VISITS_API + "/#{" + Constants.VISIT_ID + "}")
                .body(StringBody("{" +
                    "\"id\": #{" + Constants.VISIT_ID + "}," +
                    "\"date\": \"#{updatedVisitDate}\"," +
                    "\"description\": \"Updated: Follow-up appointment\"," +
                    "\"petId\": #{" + Constants.PET_ID + "}" +
                    "}"))
                .check(status().is(204))
        );

    /**
     * XÓA LỊCH KHÁM
     */
    public static ChainBuilder deleteVisit = 
        exec(
            http("Delete Visit #{" + Constants.VISIT_ID + "}")
                .delete(Constants.VISITS_API + "/#{" + Constants.VISIT_ID + "}")
                .check(status().is(204))
        );

    /**
     * LẤY THÔNG TIN LỊCH KHÁM THEO ID
     */
    public static ChainBuilder getVisitById = 
        exec(
            http("Get Visit by ID: #{" + Constants.VISIT_ID + "}")
                .get(Constants.VISITS_API + "/#{" + Constants.VISIT_ID + "}")
                .check(status().is(200))
                .check(jsonPath("$.id").exists())
                .check(jsonPath("$.date").exists())
        );

    /**
     * TẠO LỊCH KHÁM KHẨN CẤP
     */
    public static ChainBuilder createEmergencyVisit = 
        exec(session -> {
            String visitDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            return session.set("emergencyDate", visitDate);
        })
        .exec(
            http("Create Emergency Visit")
                .post("/api/owners/#{" + Constants.OWNER_ID + "}/pets/#{" + Constants.PET_ID + "}/visits")
                .body(StringBody("{" +
                    "\"date\": \"#{emergencyDate}\"," +
                    "\"description\": \"Emergency: Pet showing symptoms\"" +
                    "}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs(Constants.VISIT_ID))
        );

    /**
     * TẠO LỊCH KHÁM ĐỊNH KỲ
     */
    public static ChainBuilder createRoutineCheckup = 
        exec(session -> {
            String visitDate = LocalDate.now()
                .plusWeeks(2)
                .format(DateTimeFormatter.ISO_LOCAL_DATE);
            return session.set("checkupDate", visitDate);
        })
        .exec(
            http("Create Routine Checkup")
                .post("/api/owners/#{" + Constants.OWNER_ID + "}/pets/#{" + Constants.PET_ID + "}/visits")
                .body(StringBody("{" +
                    "\"date\": \"#{checkupDate}\"," +
                    "\"description\": \"Annual wellness checkup\"" +
                    "}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs(Constants.VISIT_ID))
        );
}
