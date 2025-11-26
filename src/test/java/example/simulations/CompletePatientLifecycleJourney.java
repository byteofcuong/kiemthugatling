package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 10: TOÀN BỘ VÒNG ĐỜI BỆNH NHÂN (Complete Patient Lifecycle Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Mô phỏng toàn bộ vòng đời: Đăng ký → Khám lần 1 → Tái khám → Đổi lịch → 
 *              Cập nhật thông tin → Chuyển pet cho owner mới → Xóa dữ liệu"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Chủ nuôi mới tìm hiểu hệ thống
 *    - Đăng ký tài khoản lần đầu
 * 
 * 2️⃣ THÂN BÀI (Action - Rất dài):
 *    Phase 1: Đăng ký và khám lần đầu
 *    Phase 2: Tái khám và cập nhật
 *    Phase 3: Đổi lịch và quản lý
 *    Phase 4: Chuyển nhượng pet (ví dụ: cho người khác)
 *    Phase 5: Dọn dẹp dữ liệu
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Xác nhận tất cả thao tác
 *    - Kết thúc vòng đời
 * 
 * Test Profile:
 * - Users: 3 concurrent users
 * - Duration: 10 minutes
 * - Pattern: Complex lifecycle
 */
public class CompletePatientLifecycleJourney extends BaseSimulation {

    ScenarioBuilder lifecycleJourney = scenario("Complete Patient Lifecycle Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Bắt đầu vòng đời hoàn chỉnh của bệnh nhân");
            return session;
        })
        
        // Phase 0: Tìm hiểu hệ thống
        .exec(VetScenario.getAllVets)
        .pause(2, 3)
        .exec(VetScenario.getAllSpecialties)
        .pause(1, 2)
        .exec(PetScenario.getAllPetTypes)
        .pause(1, 2)
        
        // ==================== PHASE 1: ĐĂNG KÝ VÀ KHÁM LẦN ĐẦU ====================
        .exec(session -> {
            System.out.println("🔵 [PHASE 1] Đăng ký tài khoản và khám lần đầu");
            return session;
        })
        
        .exec(OwnerScenario.createOwner)
        .pause(2, 3)
        .exec(OwnerScenario.getOwnerById)
        .pause(1, 2)
        .exec(PetScenario.createPetForOwner)
        .pause(2, 3)
        .exec(PetScenario.getPetById)
        .pause(1, 2)
        .exec(VisitScenario.createVisitForPet)
        .pause(2, 3)
        .exec(VisitScenario.getVisitById)
        .pause(1, 2)
        
        // ==================== PHASE 2: TÁI KHÁM VÀ CẬP NHẬT ====================
        .exec(session -> {
            System.out.println("🔵 [PHASE 2] Tái khám và cập nhật thông tin");
            return session;
        })
        
        // Sau 1 tuần, cập nhật thông tin pet (cân nặng thay đổi)
        .exec(PetScenario.updatePet)
        .pause(2, 3)
        .exec(PetScenario.getPetById)
        .pause(1, 2)
        
        // Đặt lịch tái khám
        .exec(VisitScenario.createVisitForPet)
        .pause(2, 3)
        .exec(session -> session.set("secondVisitId", session.get("visitId")))
        
        // ==================== PHASE 3: ĐỔI LỊCH VÀ QUẢN LÝ ====================
        .exec(session -> {
            System.out.println("🔵 [PHASE 3] Đổi lịch hẹn do bận việc");
            return session;
        })
        
        // Xem tất cả lịch hẹn
        .exec(VisitScenario.getAllVisits)
        .pause(2, 4)
        
        // Đổi lịch hẹn thứ 2
        .exec(session -> session.set("visitId", session.get("secondVisitId")))
        .exec(VisitScenario.updateVisit)
        .pause(2, 3)
        
        // Cập nhật thông tin owner (đổi địa chỉ)
        .exec(OwnerScenario.updateOwner)
        .pause(2, 3)
        .exec(OwnerScenario.getOwnerById)
        .pause(1, 2)
        
        // ==================== PHASE 4: CHUYỂN NHƯỢNG PET ====================
        .exec(session -> {
            System.out.println("🔵 [PHASE 4] Chuyển pet cho owner mới");
            return session;
        })
        
        // Tạo owner mới (người nhận pet)
        .exec(OwnerScenario.createOwner)
        .pause(2, 3)
        .exec(session -> session.set("newOwnerId", session.get("ownerId")))
        
        // Cập nhật pet để chuyển sang owner mới
        .exec(PetScenario.updatePet)
        .pause(2, 3)
        
        // Xác nhận pet đã chuyển owner
        .exec(PetScenario.getPetById)
        .pause(1, 2)
        
        // ==================== PHASE 5: DỌN DẸP DỮ LIỆU ====================
        .exec(session -> {
            System.out.println("🔵 [PHASE 5] Dọn dẹp và kết thúc vòng đời");
            return session;
        })
        
        // Xóa visit
        .exec(VisitScenario.deleteVisit)
        .pause(1)
        
        // Xóa pet
        .exec(PetScenario.deletePet)
        .pause(1)
        
        // Xóa owner cũ
        .exec(OwnerScenario.deleteOwner)
        .pause(1)
        
        // Xóa owner mới
        .exec(session -> session.set("ownerId", session.get("newOwnerId")))
        .exec(OwnerScenario.deleteOwner)
        .pause(1)
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] Xác nhận vòng đời hoàn tất");
            return session;
        })
        
        // Xem lại danh sách để đảm bảo đã xóa
        .exec(OwnerScenario.getAllOwners)
        .pause(1, 2)
        .exec(VisitScenario.getAllVisits)
        .pause(1, 2)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Vòng đời hoàn chỉnh của bệnh nhân đã kết thúc!");
            return session;
        });

    {
        setUp(
            lifecycleJourney.injectOpen(
                rampUsers(3).during(60),            // 3 users trong 60s
                constantUsersPerSec(0.2).during(540) // 1 user mỗi 5 giây
            )
        ).protocols(httpProtocol);
    }
}
