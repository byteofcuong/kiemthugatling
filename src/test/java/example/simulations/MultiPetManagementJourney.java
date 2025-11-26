package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 9: QUẢN LÝ NHIỀU THÚ CƯNG (Multi-Pet Management Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Chủ nuôi có 3 con thú cưng, cần đặt lịch khám định kỳ cho cả 3 con cùng ngày"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Chủ nuôi đăng nhập
 *    - Xem danh sách tất cả pets hiện có
 *    - Xem lịch sử khám của từng con
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Cập nhật thông tin cho pet 1, 2, 3 (nếu cần)
 *    - Đặt lịch khám cho pet 1
 *    - Đặt lịch khám cho pet 2 (cùng ngày)
 *    - Đặt lịch khám cho pet 3 (cùng ngày)
 *    - Kiểm tra tổng chi phí dự kiến
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Xem tất cả lịch hẹn đã đặt
 *    - In tổng hợp lịch khám
 *    - Kết thúc phiên
 * 
 * Test Profile:
 * - Users: 5 concurrent users (có nhiều pets)
 * - Duration: 7 minutes
 * - Pattern: Steady load
 */
public class MultiPetManagementJourney extends BaseSimulation {

    ScenarioBuilder multiPetJourney = scenario("Multi-Pet Management Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Chủ nuôi nhiều pet đăng nhập vào hệ thống");
            return session;
        })
        
        // Bước 1: Tạo owner
        .exec(OwnerScenario.createOwner)
        .pause(1, 2)
        
        // Bước 2: Thêm pet 1 (con chó)
        .exec(PetScenario.createPetForOwner)
        .pause(1)
        .exec(session -> session.set("pet1Id", session.get("petId")))
        
        // Bước 3: Thêm pet 2 (con mèo)
        .exec(PetScenario.createPetForOwner)
        .pause(1)
        .exec(session -> session.set("pet2Id", session.get("petId")))
        
        // Bước 4: Thêm pet 3 (con thỏ)
        .exec(PetScenario.createPetForOwner)
        .pause(1)
        .exec(session -> session.set("pet3Id", session.get("petId")))
        
        // Bước 5: Xem thông tin owner và tất cả pets
        .exec(OwnerScenario.getOwnerById)
        .pause(2, 4) // Xem danh sách 3 con
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔵 [ACTION] Đặt lịch khám định kỳ cho 3 con");
            return session;
        })
        
        // Bước 6: Xem danh sách bác sĩ
        .exec(VetScenario.getAllVets)
        .pause(2, 3) // Chọn bác sĩ có kinh nghiệm đa dạng
        
        // Bước 7: Xem chuyên khoa
        .exec(VetScenario.getAllSpecialties)
        .pause(1, 2)
        
        // Bước 8: Đặt lịch cho pet 1
        .exec(session -> session.set("petId", session.get("pet1Id")))
        .exec(VisitScenario.createVisitForPet)
        .pause(2, 3)
        
        // Bước 9: Đặt lịch cho pet 2 (cùng ngày)
        .exec(session -> session.set("petId", session.get("pet2Id")))
        .exec(VisitScenario.createVisitForPet)
        .pause(2, 3)
        
        // Bước 10: Đặt lịch cho pet 3 (cùng ngày)
        .exec(session -> session.set("petId", session.get("pet3Id")))
        .exec(VisitScenario.createVisitForPet)
        .pause(2, 3)
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] Xác nhận tất cả lịch hẹn");
            return session;
        })
        
        // Bước 11: Xem tất cả visits đã đặt
        .exec(VisitScenario.getAllVisits)
        .pause(2, 4) // Kiểm tra 3 lịch hẹn cùng ngày
        
        // Bước 12: Xem lại thông tin owner (có 3 pets và 3 visits)
        .exec(OwnerScenario.getOwnerById)
        .pause(2, 3)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Đã đặt lịch khám cho cả 3 con - Hẹn gặp!");
            return session;
        });

    {
        setUp(
            multiPetJourney.injectOpen(
                rampUsers(5).during(40),            // 5 users trong 40s
                constantUsersPerSec(0.5).during(380) // 1 user mỗi 2 giây
            )
        ).protocols(httpProtocol);
    }
}
