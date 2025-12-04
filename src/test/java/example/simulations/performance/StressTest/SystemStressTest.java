package example.simulations.performance.StressTest;

import example.config.Config;
import example.scenarios.ClinicalFlows;
import io.gatling.javaapi.core.Simulation;
import static io.gatling.javaapi.core.CoreDsl.*;

/**
 * STRESS TEST - Tìm Breaking Point (OPTIMIZED cho máy local)
 * 
 * THAY ĐỔI:
 * - Dùng CLOSED MODEL thay vì OPEN để kiểm soát số users
 * - Giảm TOTAL USERS để tránh đầy ổ đĩa
 * - Vẫn tìm được breaking point nhưng SAFE hơn
 * 
 * THỜI GIAN: 5 phút
 */
public class SystemStressTest extends Simulation {
    {
        setUp(
                ClinicalFlows.completeLifecycle.injectClosed(
                        // CLOSED MODEL = Số users cố định, không tạo vô hạn
                        
                        // PHASE 1: WARM-UP (30s) - 10 users đồng thời
                        constantConcurrentUsers(10).during(30),
                        
                        // PHASE 2: RAMP UP (60s) - Tăng 10→30 users
                        rampConcurrentUsers(10).to(30).during(60),
                        
                        // PHASE 3: MODERATE (30s) - Giữ 30 users
                        constantConcurrentUsers(30).during(30),
                        
                        // PHASE 4: PUSH HARDER (60s) - Tăng 30→50 users
                        rampConcurrentUsers(30).to(50).during(60),
                        
                        // PHASE 5: BREAKING POINT (30s) - Tăng 50→80 users
                        rampConcurrentUsers(50).to(80).during(30),
                        constantConcurrentUsers(80).during(30),
                        
                        // PHASE 6: RECOVERY (60s) - Giảm xuống test hồi phục
                        rampConcurrentUsers(80).to(10).during(60)
                )
        )
        .protocols(Config.httpProtocol)
        .assertions(
                global().responseTime().max().lt(30000),           
                global().successfulRequests().percent().gt(30.0),
                global().responseTime().percentile3().lt(15000)
        )
        .maxDuration(6 * 60); // Timeout 6 phút
    }
}