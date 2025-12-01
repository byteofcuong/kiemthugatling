package example.scenarios;

import example.api.PetApi;
import example.api.VetApi;
import example.config.Feeders; // <--- QUAN TRỌNG: Import file này
import io.gatling.javaapi.core.ScenarioBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;

public class AdminFlows {

    /**
     * 1. TUYỂN BÁC SĨ THÚ Y MỚI
     */
    public static ScenarioBuilder onboardVet = scenario("Admin: Onboard Veterinarian")
            .feed(Feeders.vets)
            .exec(VetApi.getAllVets)
            .exec(VetApi.getAllSpecialties)
            .pause(2)

            .exec(VetApi.createVet)
            .exec(VetApi.assignSpecialtyToVet)
            .exec(VetApi.updateVet)
            .pause(1)

            .exec(VetApi.getAllVets);

    /**
     * 2. MỞ RỘNG LOÀI THÚ CƯNG
     */
    public static ScenarioBuilder expandPetTypes = scenario("Admin: Expand Pet Types")
            .exec(PetApi.getAllPetTypes)
            .pause(1)
            .exec(PetApi.createPetType)
            .exec(PetApi.updatePetType)
            .exec(PetApi.getAllPetTypes);

    /**
     * 3. MỞ RỘNG CHUYÊN KHOA
     */
    public static ScenarioBuilder expandSpecialties = scenario("Admin: Expand Specialties")
            .exec(VetApi.getAllSpecialties)
            .pause(1)
            .exec(VetApi.createSpecialty)
            .exec(VetApi.updateSpecialty)
            .pause(1)
            .exec(VetApi.createVet)
            .exec(VetApi.assignSpecialtyToVet);
}