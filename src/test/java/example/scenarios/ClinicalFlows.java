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
            // Pet 2 (Vẫn dùng data cũ hoặc logic API tự xử lý nếu muốn khác biệt,
            // nhưng ở đây ta chấp nhận tạo nhiều con giống nhau cho 1 chủ để test tải)
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

    /**
     * QUẢN LÝ CHỦ SỞ HỮU CÓ NHIỀU THÚ CƯNG
     * Mô phỏng trường hợp thực tế: một gia đình nuôi nhiều thú cưng khác loại
     */
    public static ScenarioBuilder multiPetOwnerJourney = scenario("Multi-Pet Owner Journey")
            .feed(Feeders.users)
            
            // Phase 1: Tạo owner mới
            .exec(OwnerApi.createOwner)
            .pause(1, 2)
            
            // Phase 2: Xem các loại pet có sẵn
            .exec(PetApi.getAllPetTypes)
            .pause(1)
            
            // Phase 3: Đăng ký pet thứ nhất (sử dụng data từ CSV)
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet1Id", session.get("petId")))
            .pause(1)
            
            // Phase 4: Đăng ký pet thứ hai (override petName và typeId)
            .exec(session -> session
                    .set("petName", session.getString("petName") + "2")
                    .set("typeId", 1)
                    .set("typeName", "cat"))
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet2Id", session.get("petId")))
            .pause(1)
            
            // Phase 5: Đăng ký pet thứ ba
            .exec(session -> session
                    .set("petName", session.getString("firstName") + "Bird")
                    .set("typeId", 5)
                    .set("typeName", "bird"))
            .exec(PetApi.createPetForOwner)
            .exec(session -> session.set("pet3Id", session.get("petId")))
            .pause(2, 3)
            
            // Phase 6: Xem thông tin owner với tất cả pets (kiểm tra performance khi load nhiều pets)
            .exec(OwnerApi.getOwnerById)
            .pause(1, 2)
            
            // Phase 7: Tìm bác sĩ thú y
            .exec(VetApi.getAllVets)
            .pause(1)
            
            // Phase 8: Đặt lịch khám cho pet 1
            .exec(session -> session.set("petId", session.get("pet1Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(session -> session.set("visit1Id", session.get("visitId")))
            .pause(1)
            
            // Phase 9: Đặt lịch khám cho pet 2
            .exec(session -> session.set("petId", session.get("pet2Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(session -> session.set("visit2Id", session.get("visitId")))
            .pause(1)
            
            // Phase 10: Đặt lịch khám cho pet 3
            .exec(session -> session.set("petId", session.get("pet3Id")))
            .exec(VisitApi.createVisitForPet)
            .exec(session -> session.set("visit3Id", session.get("visitId")))
            .pause(2, 3)
            
            // Phase 11: Xem tất cả lịch hẹn (kiểm tra query performance)
            .exec(VisitApi.getAllVisits)
            .pause(1, 2)
            
            // Phase 12: Xem lại thông tin owner để xác nhận tất cả pets và visits
            .exec(OwnerApi.getOwnerById);
}