package example.simulations.performance.SoakTest;

import example.config.Config;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;
import java.time.Duration;

/**
 * SOAK TEST - Kiểm tra độ bền hệ thống trong thời gian dài
 * 
 * MỤC ĐÍCH:
 * 1. Phát hiện MEMORY LEAK (RAM tăng dần không giảm)
 * 2. Phát hiện CONNECTION POOL LEAK (connections không được release)
 * 3. Phát hiện DATABASE ISSUES (temp tables, locks, bloat)
 * 4. Kiểm tra GARBAGE COLLECTION có hiệu quả không
 * 
 * CHIẾN LƯỢC:
 * - Chạy với tải TRUNG BÌNH nhưng LIÊN TỤC (20 phút)
 * - Mix WRITE và READ để tạo áp lực đủ lớn
 * - Dùng CLOSED MODEL để kiểm soát resources
 * - Theo dõi metrics để phát hiện degradation
 * 
 * THỜI GIAN: 20 phút (đủ phát hiện leak mà không tốn thời gian)
 */
public class SystemSoakTest extends Simulation {
    {
        setUp(
                // LUỒNG 1: WRITE OPERATIONS - Tạo data liên tục (gây áp lực DB + Memory)
                ClinicalFlows.completeLifecycle.injectClosed(
                        // Warm-up: Tăng dần lên 15 concurrent users (2 phút)
                        rampConcurrentUsers(1).to(15).during(Duration.ofMinutes(2)),
                        
                        // Soak Phase: Giữ ổn định 15 users trong 15 phút
                        constantConcurrentUsers(15).during(Duration.ofMinutes(15)),
                        
                        // Cool-down: Giảm xuống để test recovery (3 phút)
                        rampConcurrentUsers(15).to(5).during(Duration.ofMinutes(3))
                ),
                
                // LUỒNG 2: READ OPERATIONS - Test cache và query performance
                ClinicalFlows.searchOwner.injectClosed(
                        // Warm-up
                        rampConcurrentUsers(1).to(20).during(Duration.ofMinutes(2)),
                        
                        // Soak Phase: 20 concurrent users đọc liên tục
                        constantConcurrentUsers(20).during(Duration.ofMinutes(15)),
                        
                        // Cool-down
                        rampConcurrentUsers(20).to(5).during(Duration.ofMinutes(3))
                )
        )
        .protocols(Config.httpProtocol)
        .assertions(
                // SOAK TEST yêu cầu STABILITY cao
                global().failedRequests().percent().lt(1.0),           // < 1% lỗi
                global().responseTime().percentile3().lt(5000),        // P95 < 5s (không degradation)
                global().responseTime().mean().lt(2000)                // Mean < 2s (stable performance)
        )
        .maxDuration(Duration.ofMinutes(25)); // Timeout 25 phút
    }
}
