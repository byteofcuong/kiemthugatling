package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 1: ĐĂNG KÝ BỆNH NHÂN MỚI (New Patient Registration Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Chủ nuôi mới lần đầu tiên đăng ký thông tin và đặt lịch khám cho thú cưng"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Người dùng mới vào hệ thống
 *    - Tìm hiểu về phòng khám (xem danh sách bác sĩ, chuyên khoa, loại thú cưng hỗ trợ)
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Đăng ký thông tin chủ nuôi
 *    - Thêm thông tin thú cưng
 *    - Chọn bác sĩ và chuyên khoa phù hợp
 *    - Đặt lịch khám đầu tiên
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Xác nhận thông tin đã được lưu
 *    - Kiểm tra lịch hẹn
 *    - Kết thúc phiên làm việc
 * 
 * Test Profile:
 * - Users: 10 concurrent users
 * - Duration: 5 minutes
 * - Pattern: Ramp up + Constant load
 */
public class NewPatientRegistrationJourney extends BaseSimulation {

    ScenarioBuilder newPatientJourney = scenario("New Patient Registration Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Khách hàng mới tìm hiểu về phòng khám");
            return session;
        })
        
        // Bước 1: Xem danh sách bác sĩ có sẵn
        .exec(VetScenario.getAllVets)
        .pause(2, 4) // Đọc thông tin bác sĩ
        
        // Bước 2: Xem các chuyên khoa
        .exec(VetScenario.getAllSpecialties)
        .pause(1, 3) // Đọc chuyên khoa
        
        // Bước 3: Xem các loại thú cưng được hỗ trợ
        .exec(PetScenario.getAllPetTypes)
        .pause(2, 3) // Kiểm tra hệ thống có hỗ trợ loại pet của mình không
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔵 [ACTION] Bắt đầu đăng ký thông tin");
            return session;
        })
        
        // Bước 4: Đăng ký thông tin chủ nuôi
        .exec(OwnerScenario.createOwner)
        .pause(2, 3) // Điền form và submit
        
        // Bước 5: Xác nhận thông tin chủ nuôi đã được tạo
        .exec(OwnerScenario.getOwnerById)
        .pause(1, 2) // Đọc xác nhận
        
        // Bước 6: Thêm thông tin thú cưng
        .exec(PetScenario.createPetForOwner)
        .pause(2, 4) // Điền thông tin pet (tên, ngày sinh, loại)
        
        // Bước 7: Xem lại danh sách bác sĩ để chọn
        .exec(VetScenario.getAllVets)
        .pause(2, 3) // Chọn bác sĩ phù hợp
        
        // Bước 8: Đặt lịch khám đầu tiên
        .exec(VisitScenario.createVisitForPet)
        .pause(2, 3) // Chọn ngày giờ khám
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] Xác nhận đăng ký hoàn tất");
            return session;
        })
        
        // Bước 9: Xem lại thông tin chủ nuôi và pet
        .exec(OwnerScenario.getOwnerById)
        .pause(1, 2)
        
        // Bước 10: Xác nhận lịch hẹn đã được tạo
        .exec(VisitScenario.getVisitById)
        .pause(1, 2)
        
        // Bước 11: Xem tất cả lịch hẹn của mình
        .exec(VisitScenario.getAllVisits)
        .pause(1, 2)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Đăng ký thành công - Hẹn gặp lại tại phòng khám!");
            return session;
        });

    {
        setUp(
            newPatientJourney.injectOpen(
                rampUsers(10).during(60),           // 10 users trong 60s
                constantUsersPerSec(2).during(240)  // 2 users/giây trong 4 phút
            )
        ).protocols(httpProtocol);
    }
}
