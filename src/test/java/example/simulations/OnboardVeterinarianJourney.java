package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 4: TUYỂN BÁC SĨ MỚI (Onboard New Veterinarian Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Admin tuyển bác sĩ mới, cần thêm vào hệ thống và gán chuyên khoa"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Admin đăng nhập vào hệ thống quản trị
 *    - Xem danh sách bác sĩ hiện tại
 *    - Xem danh sách chuyên khoa có sẵn
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Tạo hồ sơ bác sĩ mới
 *    - Gán chuyên khoa cho bác sĩ
 *    - Cập nhật thông tin bổ sung (nếu cần)
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Xác nhận bác sĩ đã được thêm vào hệ thống
 *    - Kiểm tra danh sách bác sĩ mới
 *    - Kết thúc phiên quản trị
 * 
 * Test Profile:
 * - Users: 3 concurrent admins
 * - Duration: 3 minutes
 * - Pattern: Low load (admin task)
 */
public class OnboardVeterinarianJourney extends BaseSimulation {

    ScenarioBuilder onboardVetJourney = scenario("Onboard New Veterinarian Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Admin kiểm tra hệ thống hiện tại");
            return session;
        })
        
        // Bước 1: Xem tất cả bác sĩ hiện có
        .exec(VetScenario.getAllVets)
        .pause(2, 4) // Đếm số lượng bác sĩ, xem phân bố chuyên khoa
        
        // Bước 2: Xem tất cả chuyên khoa có sẵn
        .exec(VetScenario.getAllSpecialties)
        .pause(2, 3) // Xác định chuyên khoa cần bổ sung nhân sự
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔵 [ACTION] Bắt đầu tạo hồ sơ bác sĩ mới");
            return session;
        })
        
        // Bước 3: Tạo bác sĩ mới
        .exec(VetScenario.createVet)
        .pause(2, 4) // Điền form: Họ tên, số điện thoại, email, chứng chỉ
        
        // Bước 4: Gán chuyên khoa cho bác sĩ
        .exec(VetScenario.assignSpecialtyToVet)
        .pause(2, 3) // Chọn 1 hoặc nhiều chuyên khoa
        
        // Bước 5: Cập nhật thông tin bổ sung
        .exec(VetScenario.updateVet)
        .pause(2, 3) // Thêm kinh nghiệm, giờ làm việc, ghi chú
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] Xác nhận bác sĩ mới");
            return session;
        })
        
        // Bước 6: Xem danh sách bác sĩ mới (bao gồm người vừa thêm)
        .exec(VetScenario.getAllVets)
        .pause(2, 3) // Tìm tên bác sĩ mới trong danh sách
        
        // Bước 7: Xem lại danh sách chuyên khoa (số lượng bác sĩ đã tăng)
        .exec(VetScenario.getAllSpecialties)
        .pause(1, 2)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Bác sĩ mới đã sẵn sàng phục vụ!");
            return session;
        });

    {
        setUp(
            onboardVetJourney.injectOpen(
                rampUsers(3).during(20),            // 3 admin trong 20s
                constantUsersPerSec(0.5).during(160) // 1 admin mỗi 2 giây
            )
        ).protocols(httpProtocol);
    }
}
