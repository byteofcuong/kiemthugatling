package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 3: ĐỔI LỊCH HẸN KHÁM BỆNH (Reschedule Visit Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Chủ nuôi có việc bận, cần đổi lịch hẹn khám bệnh sang ngày khác"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Đăng nhập vào hệ thống
 *    - Xem danh sách lịch hẹn hiện tại
 *    - Tìm lịch hẹn cần đổi
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Xem chi tiết lịch hẹn cũ
 *    - Cập nhật thời gian mới
 *    - Có thể cập nhật lý do khám
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Xác nhận lịch hẹn đã được cập nhật
 *    - Xem lại tất cả lịch hẹn
 *    - Kết thúc phiên
 * 
 * Test Profile:
 * - Users: 6 concurrent users
 * - Duration: 4 minutes
 * - Pattern: Ramp up
 */
public class RescheduleVisitJourney extends BaseSimulation {

    ScenarioBuilder rescheduleJourney = scenario("Reschedule Visit Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Chủ nuôi đăng nhập để xem lịch hẹn");
            return session;
        })
        
        // Setup: Tạo dữ liệu ban đầu (owner + pet + visit)
        .exec(OwnerScenario.createOwner)
        .pause(1)
        .exec(PetScenario.createPetForOwner)
        .pause(1)
        .exec(VisitScenario.createVisitForPet)
        .pause(2, 3)
        
        // Bước 1: Xem tất cả lịch hẹn
        .exec(VisitScenario.getAllVisits)
        .pause(2, 4) // Tìm lịch hẹn cần đổi
        
        // Bước 2: Xem chi tiết lịch hẹn cụ thể
        .exec(VisitScenario.getVisitById)
        .pause(2, 3) // Đọc thông tin: ngày giờ, lý do khám, bác sĩ
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔵 [ACTION] Bắt đầu đổi lịch hẹn");
            return session;
        })
        
        // Bước 3: Xem danh sách bác sĩ (trường hợp muốn đổi bác sĩ)
        .exec(VetScenario.getAllVets)
        .pause(1, 2)
        
        // Bước 4: Cập nhật lịch hẹn (đổi ngày giờ, cập nhật lý do)
        .exec(VisitScenario.updateVisit)
        .pause(2, 4) // Chọn ngày mới, sửa mô tả
        
        // Bước 5: Xem lại lịch hẹn đã cập nhật
        .exec(VisitScenario.getVisitById)
        .pause(1, 2) // Kiểm tra thông tin mới
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] Xác nhận lịch hẹn mới");
            return session;
        })
        
        // Bước 6: Xem tất cả lịch hẹn để đảm bảo không trùng
        .exec(VisitScenario.getAllVisits)
        .pause(2, 3)
        
        // Bước 7: Xem thông tin pet (kiểm tra lịch sử khám)
        .exec(PetScenario.getPetById)
        .pause(1, 2)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Đổi lịch hẹn thành công!");
            return session;
        });

    {
        setUp(
            rescheduleJourney.injectOpen(
                rampUsers(6).during(30),            // 6 users trong 30s
                constantUsersPerSec(1).during(210)  // 1 user/giây trong 3.5 phút
            )
        ).protocols(httpProtocol);
    }
}
