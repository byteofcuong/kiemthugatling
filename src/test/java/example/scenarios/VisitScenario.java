package example.scenarios;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import example.Constants;
import io.gatling.javaapi.core.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * VISIT Management Scenario - Schedule and manage vet visits
 * Simulates booking appointments for pets
 */
public class VisitScenario {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Create a new visit for an existing pet
     * Requires: ownerId and petId in session
     */
    public static ChainBuilder createVisit = 
        exec(session -> {
            String visitDate = LocalDate.now().plusDays(7).format(DATE_FORMATTER);
            return session.set("visitDate", visitDate);
        })
        .exec(
            http("Schedule Visit for Pet #{" + Constants.PET_ID + "}")
                .post(Constants.OWNERS_API + "/#{" + Constants.OWNER_ID + "}/pets/#{" + Constants.PET_ID + "}/visits")
                .body(StringBody("{" +
                    "\"date\": \"#{visitDate}\"," +
                    "\"description\": \"Annual checkup and vaccination\"" +
                    "}"))
                .check(status().is(201))
                .check(jsonPath("$.id").saveAs("visitId"))
        )
        .pause(1, 2);

    /**
     * View all visits
     */
    public static ChainBuilder viewAllVisits = 
        exec(
            http("View All Visits")
                .get(Constants.VISITS_API)
                .check(status().is(200))
                .check(jsonPath("$[*].id").findAll().saveAs("visitIds"))
        )
        .pause(2, 3);

    /**
     * Full visit workflow: Create Owner → Create Pet → Schedule Visit
     */
    public static ChainBuilder fullVisitWorkflow = 
        exec(OwnerPetScenario.createOwnerAndPet)
        .exec(createVisit)
        .exec(viewAllVisits);
}
