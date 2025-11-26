package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 5: MỞ RỘNG CHUYÊN KHOA (Expand Clinic Specialties Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Admin mở thêm chuyên khoa mới (ví dụ: Chỉnh hình, Tim mạch) và gán bác sĩ phụ trách"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Admin phân tích nhu cầu khách hàng
 *    - Xem danh sách chuyên khoa hiện tại
 *    - Xem danh sách bác sĩ có sẵn
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Tạo chuyên khoa mới
 *    - Cập nhật mô tả chuyên khoa
 *    - Tạo bác sĩ mới hoặc gán bác sĩ hiện có
 *    - Gán chuyên khoa cho bác sĩ
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Xác nhận chuyên khoa đã active
 *    - Kiểm tra bác sĩ đã được gán
 *    - Kết thúc cấu hình
 * 
 * Test Profile:
 * - Users: 2 concurrent admins
 * - Duration: 4 minutes
 * - Pattern: Sequential tasks
 */
public class ExpandSpecialtiesJourney extends BaseSimulation {

    ScenarioBuilder expandSpecialtiesJourney = scenario("Expand Clinic Specialties Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Admin phân tích nhu cầu mở rộng chuyên khoa");
            return session;
        })
        
        // Bước 1: Xem danh sách chuyên khoa hiện tại
        .exec(VetScenario.getAllSpecialties)
        .pause(2, 4) // Kiểm tra xem đã có chuyên khoa nào, thiếu cái gì
        
        // Bước 2: Xem danh sách bác sĩ hiện có
        .exec(VetScenario.getAllVets)
        .pause(2, 3) // Xem có bác sĩ nào có thể gán vào chuyên khoa mới không
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔵 [ACTION] Tạo chuyên khoa mới");
            return session;
        })
        
        // Bước 3: Tạo chuyên khoa mới (ví dụ: "Cardiology" - Tim mạch)
        .exec(VetScenario.createSpecialty)
        .pause(2, 3) // Điền tên, mô tả chuyên khoa
        
        // Bước 4: Cập nhật mô tả chi tiết chuyên khoa
        .exec(VetScenario.updateSpecialty)
        .pause(2, 3) // Thêm thông tin: Dịch vụ, giá cả, thiết bị
        
        // Bước 5: Tạo bác sĩ mới cho chuyên khoa này
        .exec(VetScenario.createVet)
        .pause(2, 4) // Tuyển bác sĩ chuyên về tim mạch
        
        // Bước 6: Gán chuyên khoa mới cho bác sĩ
        .exec(VetScenario.assignSpecialtyToVet)
        .pause(2, 3)
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] Xác nhận chuyên khoa mới đang hoạt động");
            return session;
        })
        
        // Bước 7: Kiểm tra danh sách chuyên khoa (đã có chuyên khoa mới)
        .exec(VetScenario.getAllSpecialties)
        .pause(2, 3)
        
        // Bước 8: Kiểm tra danh sách bác sĩ (bác sĩ mới đã có specialty)
        .exec(VetScenario.getAllVets)
        .pause(2, 3)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Chuyên khoa mới đã sẵn sàng phục vụ khách hàng!");
            return session;
        });

    {
        setUp(
            expandSpecialtiesJourney.injectOpen(
                rampUsers(2).during(20),            // 2 admin trong 20s
                constantUsersPerSec(0.3).during(220) // 1 admin mỗi 3 giây
            )
        ).protocols(httpProtocol);
    }
}
