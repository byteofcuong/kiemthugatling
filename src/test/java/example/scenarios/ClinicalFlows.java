package example.scenarios;

import example.api.OwnerApi;
import example.api.PetApi;
import example.api.VetApi;
import example.api.VisitApi;
import example.config.Feeders;
import io.gatling.javaapi.core.ScenarioBuilder;
import static io.gatling.javaapi.core.CoreDsl.*;

public class ClinicalFlows {

    /**
     * ĐĂNG KÍ BỆNH NHÂN MỚI
     */
    public static ScenarioBuilder newPatientRegistration = scenario("New Patient Registration")
            .feed(Feeders.users)
            .exec(VetApi.getAllVets)
            .exec(VetApi.getAllSpecialties)
            .exec(PetApi.getAllPetTypes)
            .pause(2)
            .exec(OwnerApi.createOwner)
            .pause(1)
            .exec(OwnerApi.getOwnerById)
            .exec(PetApi.createPetForOwner)
            .pause(1)
            .exec(VisitApi.createVisitForPet)
            .exec(VisitApi.getVisitById);

    /**
     * CHỦ SỞ HỮU HIỆN TẠI THÊM THÚ CƯNG MỚI
     */
    public static ScenarioBuilder existingOwnerAddPet = scenario("Existing Owner Add Pet")
            .feed(Feeders.users)
            .exec(OwnerApi.createOwner)
            .exec(OwnerApi.getOwnerById)
            .pause(1)
            .exec(PetApi.getAllPetTypes)
            .exec(PetApi.createPetForOwner)
            .pause(2)
            .exec(VetApi.getAllVets)
            .exec(VisitApi.createVisitForPet)
            .exec(OwnerApi.getOwnerById);

    /**
     * LÊN LỊCH KHÁM MỚI
     */
    public static ScenarioBuilder rescheduleVisit = scenario("Reschedule Visit")
            .feed(Feeders.users)
            .exec(OwnerApi.createOwner)
            .exec(PetApi.createPetForOwner)
            .exec(VisitApi.createVisitForPet)
            .pause(1)
            .exec(VisitApi.getAllVisits)
            .exec(VisitApi.getVisitById)
            .exec(VetApi.getAllVets)
            .pause(2)
            .exec(VisitApi.updateVisit)
            .exec(VisitApi.getVisitById);

    /**
     * KHÁM CẤP CỨU
     */
    public static ScenarioBuilder emergencyVisit = scenario("Emergency Visit")
            .feed(Feeders.users)
            .exec(VetApi.getAllVets)
            .pause(1)
            .exec(OwnerApi.createOwner)
            .pause(1)
            .exec(OwnerApi.getOwnerById)
            .exec(PetApi.createPetForOwner)
            .pause(1)
            .exec(VisitApi.createEmergencyVisit)
            .exec(VisitApi.getVisitById);

    /**
     * KHÁM ĐỊNH KỲ
     */
    public static ScenarioBuilder walkInVisit = scenario("Walk-in Visit")
            .feed(Feeders.users)
            .exec(OwnerApi.getAllOwners)
            .exec(VetApi.getAllVets)
            .pause(1)
            .exec(OwnerApi.createOwner)
            .exec(PetApi.createPetForOwner)
            .exec(VisitApi.createVisitForPet)
            .exec(VisitApi.getVisitById);

    /**
     * QUẢN LÝ NHIỀU THÚ CƯNG
     */
    public static ScenarioBuilder multiPetManagement = scenario("Multi-Pet Management")
            .feed(Feeders.users)
            .exec(OwnerApi.createOwner)
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet1Id", session.get("petId")))
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet2Id", session.get("petId")))
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet3Id", session.get("petId")))
            .exec(session -> session.set("petId", session.get("pet1Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(session -> session.set("petId", session.get("pet2Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(session -> session.set("petId", session.get("pet3Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(VisitApi.getAllVisits);

    /**
     * QUY TRÌNH TOÀN DIỆN CỦA BỆNH NHÂN
     */
    public static ScenarioBuilder completeLifecycle = scenario("Complete Patient Lifecycle")
            .pace(5)
            .feed(Feeders.users)
            .exec(OwnerApi.createOwner)
            .pause(1, 2)
            .exec(PetApi.createPetForOwner)
            .pause(1, 2)
            .exec(VisitApi.createVisitForPet)
            .pause(2, 3)
            .exec(PetApi.updatePet)
            .pause(1, 2)
            .exec(VisitApi.updateVisit)
            .pause(1, 2)
            .exec(OwnerApi.updateOwner)
            .pause(1, 2)
            .exec(VisitApi.deleteVisit)
            .pause(1)
            .exec(PetApi.deletePet)
            .pause(1)
            .exec(OwnerApi.deleteOwner);

    /**
     * TÌM KIẾM CHỦ SỞ HỮU THEO LAST NAME
     */
    public static ScenarioBuilder searchOwner = scenario("Read: Search Owner")
            .feed(Feeders.users)
            .exec(OwnerApi.createOwner)
            .exec(session -> session.set("searchName", session.getString("lastName")))
            .pause(1)
            .exec(OwnerApi.searchOwnerByLastName);

    /**
     * ĐĂNG KÍ VỚI DỮ LIỆU KHÔNG HỢP LỆ
     */
    public static ScenarioBuilder invalidRegistration = scenario("Negative: Invalid Registration")
            .exec(OwnerApi.createOwnerInvalidData)
            .pause(1);

    /**
     * QUẢN LÝ CHỦ SỞ HỮU CÓ NHIỀU THÚ CƯNG
     */
    public static ScenarioBuilder multiPetOwnerJourney = scenario("Multi-Pet Owner Journey")
            .feed(Feeders.users)
            .exec(OwnerApi.createOwner)
            .pause(1, 2)
            .exec(PetApi.getAllPetTypes)
            .pause(1)
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet1Id", session.get("petId")))
            .pause(1)
            .exec(session -> session
                    .set("petName", session.getString("petName") + "2")
                    .set("typeId", 1)
                    .set("typeName", "cat"))
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet2Id", session.get("petId")))
            .pause(1)
            .exec(session -> session
                    .set("petName", session.getString("firstName") + "Bird")
                    .set("typeId", 5)
                    .set("typeName", "bird"))
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet3Id", session.get("petId")))
            .pause(2, 3)
            .exec(OwnerApi.getOwnerById)
            .pause(1, 2)
            .exec(VetApi.getAllVets)
            .pause(1)
            .exec(session -> session.set("petId", session.get("pet1Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(session -> session.set("visit1Id", session.get("visitId")))
            .pause(1)
            .exec(session -> session.set("petId", session.get("pet2Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(session -> session.set("visit2Id", session.get("visitId")))
            .pause(1)
            .exec(session -> session.set("petId", session.get("pet3Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(session -> session.set("visit3Id", session.get("visitId")))
            .pause(2, 3)
            .exec(VisitApi.getAllVisits)
            .pause(1, 2)
            .exec(OwnerApi.getOwnerById);
}