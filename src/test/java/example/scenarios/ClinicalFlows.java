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
            .exec(OwnerApi.createOwner)
            .exec(PetApi.createPetForOwner)
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
            // Pet 1 (Dùng data từ CSV)
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet1Id", session.get("petId")))
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet2Id", session.get("petId")))
            // Pet 3
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet3Id", session.get("petId")))
            // Đặt lịch
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
            .feed(Feeders.users)
            .exec(OwnerApi.createOwner)
            .exec(PetApi.createPetForOwner)
            .exec(VisitApi.createVisitForPet)
            .pause(2)
            .exec(PetApi.updatePet)
            .exec(VisitApi.updateVisit)
            .exec(OwnerApi.updateOwner)
            .exec(VisitApi.deleteVisit)
            .exec(PetApi.deletePet)
            .exec(OwnerApi.deleteOwner);

    /**
     * TÌM KIẾM CHỦ SỞ HỮU THEO LAST NAME
     */
    public static ScenarioBuilder searchOwner = scenario("Read: Search Owner")
            .feed(Feeders.users) // <--- NẠP DATA để lấy lastName
            .exec(OwnerApi.createOwner)
            // Lưu lại lastName vừa tạo để tìm kiếm chính xác
            .exec(session -> session.set("searchName", session.getString("lastName")))
            .pause(1)
            .exec(OwnerApi.searchOwnerByLastName);

    /**
     * ĐĂNG KÍ VỚI DỮ LIỆU KHÔNG HỢP LỆ
     */
    public static ScenarioBuilder invalidRegistration = scenario("Negative: Invalid Registration")
            // KHÔNG CẦN FEEDER vì dùng chuỗi string cứng để test lỗi
            .exec(OwnerApi.createOwnerInvalidData)
            .pause(1);
}