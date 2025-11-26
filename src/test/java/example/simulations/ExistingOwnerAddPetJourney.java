package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 2: CHỦ NUÔI CŨ THÊM THÚ CƯNG MỚI (Existing Owner Add Pet Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Chủ nuôi hiện tại vừa mua thêm thú cưng mới, muốn thêm vào hệ thống và đặt lịch tiêm phòng"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Chủ nuôi đã có tài khoản đăng nhập vào hệ thống
 *    - Xem lại thông tin cá nhân và danh sách pet hiện tại
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Xem các loại pet được hỗ trợ
 *    - Thêm thông tin pet mới
 *    - Xem danh sách bác sĩ
 *    - Đặt lịch tiêm phòng cho pet mới
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Xác nhận pet mới đã được thêm
 *    - Kiểm tra lịch tiêm phòng
 *    - Xem tổng quan tất cả pets
 * 
 * Test Profile:
 * - Users: 8 concurrent users
 * - Duration: 6 minutes
 * - Pattern: Steady load
 */
public class ExistingOwnerAddPetJourney extends BaseSimulation {

    ScenarioBuilder addPetJourney = scenario("Existing Owner Add Pet Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Chủ nuôi hiện tại đăng nhập vào hệ thống");
            return session;
        })
        
        // Bước 1: Tạo owner (simulate existing user login)
        .exec(OwnerScenario.createOwner)
        .pause(1, 2)
        
        // Bước 2: Xem thông tin tài khoản và pets hiện tại
        .exec(OwnerScenario.getOwnerById)
        .pause(2, 4) // Đọc thông tin cá nhân, xem có bao nhiêu pet
        
        // Bước 3: Xem danh sách tất cả owners (để thấy mình trong hệ thống)
        .exec(OwnerScenario.getAllOwners)
        .pause(1, 2)
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔵 [ACTION] Bắt đầu thêm thú cưng mới");
            return session;
        })
        
        // Bước 4: Kiểm tra các loại pet được hỗ trợ
        .exec(PetScenario.getAllPetTypes)
        .pause(2, 3) // Chọn loại pet mới (chó, mèo, chim...)
        
        // Bước 5: Thêm pet mới vào tài khoản
        .exec(PetScenario.createPetForOwner)
        .pause(3, 5) // Điền form đầy đủ (tên, ngày sinh, loại, màu lông...)
        
        // Bước 6: Xem thông tin pet vừa tạo
        .exec(PetScenario.getPetById)
        .pause(1, 2) // Kiểm tra thông tin đã đúng chưa
        
        // Bước 7: Xem danh sách bác sĩ (có thể chọn bác sĩ quen hoặc mới)
        .exec(VetScenario.getAllVets)
        .pause(2, 4) // Đọc thông tin bác sĩ, chọn người phù hợp
        
        // Bước 8: Đặt lịch tiêm phòng cho pet mới
        .exec(VisitScenario.createVisitForPet)
        .pause(2, 3) // Chọn ngày giờ tiêm phòng
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] Xác nhận pet mới và lịch tiêm phòng");
            return session;
        })
        
        // Bước 9: Xem lại thông tin pet mới
        .exec(PetScenario.getPetById)
        .pause(1, 2)
        
        // Bước 10: Xác nhận lịch tiêm phòng
        .exec(VisitScenario.getVisitById)
        .pause(1, 2)
        
        // Bước 11: Xem tổng quan thông tin owner (bao gồm tất cả pets)
        .exec(OwnerScenario.getOwnerById)
        .pause(2, 3)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Đã thêm pet mới và đặt lịch tiêm phòng thành công!");
            return session;
        });

    {
        setUp(
            addPetJourney.injectOpen(
                rampUsers(8).during(40),            // 8 users trong 40s
                constantUsersPerSec(1).during(320)  // 1 user/giây trong 5 phút 20s
            )
        ).protocols(httpProtocol);
    }
}
