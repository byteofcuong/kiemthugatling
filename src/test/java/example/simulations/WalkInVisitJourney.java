package example.simulations;

import static io.gatling.javaapi.core.CoreDsl.*;

import example.BaseSimulation;
import example.scenarios.*;
import io.gatling.javaapi.core.*;

/**
 * KỊCH BẢN 8: WALK-IN KHÁM KHÔNG HẸN (Walk-in Visit Journey)
 * 
 * ❓ Câu hỏi: "Người dùng vào hệ thống để làm xong việc gì?"
 * ✅ Trả lời: "Chủ nuôi đã ở phòng khám, lễ tân cần đăng ký thông tin nhanh cho khách walk-in"
 * 
 * === CẤU TRÚC KỊCH BẢN ===
 * 
 * 1️⃣ MỞ BÀI (Init):
 *    - Khách hàng walk-in đến phòng khám
 *    - Lễ tân kiểm tra khách cũ hay mới
 *    - Xem bác sĩ nào đang rảnh
 * 
 * 2️⃣ THÂN BÀI (Action):
 *    - Nếu khách mới: Đăng ký owner + pet
 *    - Nếu khách cũ: Tìm thông tin và cập nhật
 *    - Tạo visit cho ca khám walk-in
 *    - Ghi chú: "Walk-in - No appointment"
 * 
 * 3️⃣ KẾT BÀI (Teardown):
 *    - In phiếu khám
 *    - Khách chờ gặp bác sĩ
 *    - Kết thúc phiên đăng ký
 * 
 * Test Profile:
 * - Users: 8 concurrent walk-ins
 * - Duration: 3 minutes
 * - Pattern: Random spikes (walk-in không đoán trước)
 */
public class WalkInVisitJourney extends BaseSimulation {

    ScenarioBuilder walkInJourney = scenario("Walk-in Visit Journey")
        
        // ==================== MỞ BÀI: INIT ====================
        .exec(session -> {
            System.out.println("🟢 [INIT] Khách walk-in đến phòng khám - Lễ tân kiểm tra");
            return session;
        })
        
        // Bước 1: Kiểm tra danh sách owners (tìm xem khách cũ không)
        .exec(OwnerScenario.getAllOwners)
        .pause(1, 2) // Tìm nhanh trong hệ thống
        
        // Bước 2: Xem bác sĩ nào đang available
        .exec(VetScenario.getAllVets)
        .pause(1) // Check lịch bác sĩ
        
        // ==================== THÂN BÀI: ACTION ====================
        .exec(session -> {
            System.out.println("🔵 [ACTION] Đăng ký nhanh cho khách walk-in");
            return session;
        })
        
        // Bước 3: Tạo owner mới (hoặc lấy existing)
        .exec(OwnerScenario.createOwner)
        .pause(1, 2) // Hỏi thông tin nhanh: Tên, SĐT
        
        // Bước 4: Kiểm tra owner vừa tạo
        .exec(OwnerScenario.getOwnerById)
        .pause(1)
        
        // Bước 5: Thêm pet
        .exec(PetScenario.createPetForOwner)
        .pause(1, 2) // Ghi thông tin pet: Tên, loại, triệu chứng
        
        // Bước 6: Tạo visit WALK-IN
        .exec(VisitScenario.createVisitForPet)
        .pause(1) // Ghi: "Walk-in - No prior appointment"
        
        // ==================== KẾT BÀI: TEARDOWN ====================
        .exec(session -> {
            System.out.println("🟡 [TEARDOWN] In phiếu khám và hướng dẫn khách");
            return session;
        })
        
        // Bước 7: Xác nhận visit đã tạo
        .exec(VisitScenario.getVisitById)
        .pause(1)
        
        // Bước 8: Xem tất cả visits (để biết khách phải chờ bao lâu)
        .exec(VisitScenario.getAllVisits)
        .pause(1)
        
        .exec(session -> {
            System.out.println("✅ [COMPLETE] Phiếu khám đã in - Khách vui lòng chờ gặp bác sĩ!");
            return session;
        });

    {
        setUp(
            walkInJourney.injectOpen(
                atOnceUsers(3),                     // 3 khách walk-in cùng lúc
                rampUsers(5).during(30),            // 5 khách trong 30s
                constantUsersPerSec(1).during(150)  // 1 khách/giây
            )
        ).protocols(httpProtocol);
    }
}
