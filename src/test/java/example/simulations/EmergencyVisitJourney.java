package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 7: KHÁM KHẨN CẤP (Emergency Visit Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Thú cưng gặp tai nạn/bệnh nặng, chủ nuôi cần đặt lịch khám khẩn cấp ngay lập tức"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Tình huống khẩn cấp xảy ra
 *    - Chủ nuôi truy cập hệ thống nhanh chóng
 *    - Xem danh sách bác sĩ khả dụng NGAY
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Đăng ký nhanh (nếu chưa có tài khoản)
 *    - Thêm thông tin pet tối thiểu
 *    - Đặt lịch khám EMERGENCY ngay lập tức
 *    - Không có pause lâu (tình huống khẩn)
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - Nhận xác nhận lịch khám
 *    - Xem thông tin bác sĩ và địa chỉ phòng khám
 *    - Kết thúc để đưa pet đến phòng khám
 * 
 * Test Profile:
 * - Users: 5 concurrent emergencies
 * - Duration: 2 minutes
 * - Pattern: Burst load (tình huống khẩn cấp, tất cả cùng lúc)
 */
public class EmergencyVisitJourney extends BaseSimulation {

    ScenarioBuilder emergencyJourney = scenario("Emergency Visit Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🚨 [EMERGENCY] Tình huống khẩn cấp - Pet gặp tai nạn!");
            return session;
        })
        
        // Bước 1: Xem danh sách bác sĩ NGAY (không chọn lựa nhiều)
        .exec(VetScenario.getAllVets)
        .pause(1, 2) // Chọn nhanh bác sĩ đầu tiên có sẵn
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔴 [ACTION] Đăng ký khẩn cấp trong vòng 30 giây");
            return session;
        })
        
        // Bước 2: Đăng ký owner nhanh (chỉ điền thông tin bắt buộc)
        .exec(OwnerScenario.createOwner)
        .pause(1) // Điền nhanh: Tên, SĐT, địa chỉ
        
        // Bước 3: Thêm pet (chỉ thông tin cơ bản)
        .exec(PetScenario.createPetForOwner)
        .pause(1) // Tên pet, loại, triệu chứng khẩn cấp
        
        // Bước 4: Đặt lịch khám EMERGENCY
        .exec(VisitScenario.createVisitForPet)
        .pause(1) // Ghi rõ: EMERGENCY, mô tả triệu chứng
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [CONFIRM] Nhận xác nhận - Chuẩn bị đưa pet đến phòng khám");
            return session;
        })
        
        // Bước 5: Xác nhận lịch khám ngay
        .exec(VisitScenario.getVisitById)
        .pause(1) // Kiểm tra thời gian, bác sĩ
        
        // Bước 6: Xem thông tin bác sĩ (để biết phòng khám ở đâu)
        .exec(VetScenario.getAllVets)
        .pause(1)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Lịch khám khẩn cấp đã được xác nhận - Hãy đến ngay!");
            return session;
        });

    {
        setUp(
            emergencyJourney.injectOpen(
                atOnceUsers(5),                     // 5 emergencies cùng lúc
                rampUsers(3).during(20),            // Thêm 3 cases trong 20s
                constantUsersPerSec(1).during(100)  // 1 emergency/giây
            )
        ).protocols(httpProtocol);
    }
}
