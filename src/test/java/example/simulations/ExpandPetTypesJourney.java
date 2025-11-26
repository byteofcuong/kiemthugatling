package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 6: HỖ TRỢ LOẠI THÚ CƯNG MỚI (Expand Pet Type Support Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Admin mở rộng dịch vụ, thêm hỗ trợ cho loại thú cưng mới (bò sát, chim, thỏ...)"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Admin xem danh sách pet types hiện tại
 *    - Phân tích nhu cầu thị trường
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Tạo pet type mới (ví dụ: "Reptile" - Bò sát)
 *    - Cập nhật thông tin pet type
 *    - Test bằng cách tạo owner và pet mẫu
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Xác nhận pet type mới hoạt động
 *    - Xóa dữ liệu test
 *    - Kết thúc cấu hình
 * 
 * Test Profile:
 * - Users: 2 concurrent admins
 * - Duration: 3 minutes
 * - Pattern: Low load
 */
public class ExpandPetTypesJourney extends BaseSimulation {

    ScenarioBuilder expandPetTypesJourney = scenario("Expand Pet Type Support Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Admin kiểm tra các loại thú cưng hiện hỗ trợ");
            return session;
        })
        
        // Bước 1: Xem danh sách pet types hiện tại
        .exec(PetScenario.getAllPetTypes)
        .pause(2, 4) // Kiểm tra xem đã có: chó, mèo, chim... thiếu gì?
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔵 [ACTION] Thêm loại thú cưng mới vào hệ thống");
            return session;
        })
        
        // Bước 2: Tạo pet type mới (ví dụ: "Rabbit" - Thỏ)
        .exec(PetScenario.createPetType)
        .pause(2, 3) // Điền tên loại pet
        
        // Bước 3: Cập nhật mô tả pet type
        .exec(PetScenario.updatePetType)
        .pause(2, 3) // Thêm thông tin: Đặc điểm, yêu cầu chăm sóc
        
        // Bước 4: Test - Tạo owner mẫu
        .exec(OwnerScenario.createOwner)
        .pause(1, 2)
        
        // Bước 5: Test - Tạo pet với loại mới vừa thêm
        .exec(PetScenario.createPetForOwner)
        .pause(2, 3) // Kiểm tra có thể chọn pet type mới không
        
        // Bước 6: Xem thông tin pet vừa tạo
        .exec(PetScenario.getPetById)
        .pause(1, 2) // Đảm bảo pet type hiển thị đúng
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] Dọn dẹp dữ liệu test và xác nhận");
            return session;
        })
        
        // Bước 7: Xóa pet test
        .exec(PetScenario.deletePet)
        .pause(1)
        
        // Bước 8: Xóa owner test
        .exec(OwnerScenario.deleteOwner)
        .pause(1)
        
        // Bước 9: Xem lại danh sách pet types (đã có loại mới)
        .exec(PetScenario.getAllPetTypes)
        .pause(2, 3)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Loại thú cưng mới đã sẵn sàng!");
            return session;
        });

    {
        setUp(
            expandPetTypesJourney.injectOpen(
                rampUsers(2).during(15),            // 2 admin trong 15s
                constantUsersPerSec(0.5).during(165) // 1 admin mỗi 2 giây
            )
        ).protocols(httpProtocol);
    }
}
