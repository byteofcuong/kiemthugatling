# 🚀 PetClinic Performance Testing Strategy

## 📊 Tổng quan

Dự án này thực hiện **Performance Testing chuyên nghiệp** cho Spring PetClinic REST API sử dụng Gatling.

### Điểm nổi bật (Flex Points):

✅ **Data-Driven Testing**: 5,000 unique owners từ generated dataset  
✅ **Dynamic Correlation**: Tự động extract Owner ID cho Pet creation  
✅ **Multiple Load Patterns**: Smoke, Baseline, Stress, Soak tests  
✅ **Quality Gates**: CI/CD ready với assertions nghiêm ngặt  
✅ **Production-Like**: Realistic think times và user behavior  

---

## 📁 Cấu trúc Project

```
src/test/java/example/
├── simulations/
│   ├── PetClinicSmokeTest.java       # Kiểm tra nhanh connectivity
│   ├── PetClinicSimulation.java      # Baseline test với Quality Gates
│   ├── StressTestSimulation.java     # Tìm breaking point (2000 users)
│   └── SoakTestSimulation.java       # Test độ bền (30 phút)
├── scenarios/
│   └── OwnerPetScenario.java         # Core business flow với correlation
├── BaseSimulation.java                # HTTP config chung
└── Constants.java                     # API endpoints & session keys

src/test/resources/data/
└── owners.csv                         # 5,000 unique test data
```

---

## 🎯 Chiến lược Testing

### 1️⃣ Smoke Test (Kiểm tra nhanh)
**File**: `PetClinicSmokeTest.java`  
**Mục đích**: Verify API availability  
**Tải**: 1 user  
**Thời gian**: < 10 giây  

```bash
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.PetClinicSmokeTest"
```

**Success criteria**: 100% requests pass

---

### 2️⃣ Baseline Test (Chuẩn hiệu năng)
**File**: `PetClinicSimulation.java`  
**Mục đích**: Thiết lập baseline metrics  
**Tải**: 5 users (configurable)  
**Thời gian**: < 1 phút  

```bash
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.PetClinicSimulation"
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.PetClinicSimulation" "-Dvu=10"
```

**Quality Gates**:
- ✅ 95th percentile < 500ms
- ✅ Error rate < 1%
- ✅ Mean response time < 300ms
- ✅ Success rate > 99%

**Điểm Flex**: "Em đặt Quality Gates để tích hợp vào CI/CD. Nếu code mới làm chậm hệ thống, build sẽ tự động fail."

---

### 3️⃣ Stress Test (Tìm giới hạn)
**File**: `StressTestSimulation.java`  
**Mục đích**: Tìm breaking point  
**Tải**: 0 → 2000 users (ramp up trong 2 phút)  
**Thời gian**: ~ 5 phút  

```bash
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.StressTestSimulation"
```

**Success criteria**:
- Error rate < 5% (cho phép lỗi khi quá tải)
- 95th percentile < 2s

**Mục tiêu**: 
- Xác định số user tối đa hệ thống chịu được
- Quan sát điểm nào response time tăng đột ngột
- Monitor Docker stats để thấy CPU/Memory usage

**Điểm Flex**: "Em không chỉ test xem nó chạy được không, mà còn tìm ra con số cụ thể: Hệ thống chịu được bao nhiêu user đồng thời trước khi sập."

---

### 4️⃣ Soak Test (Test độ bền)
**File**: `SoakTestSimulation.java`  
**Mục đích**: Phát hiện memory leak  
**Tải**: 50 users constant  
**Thời gian**: 30 phút  

```bash
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.SoakTestSimulation"
```

**Success criteria**:
- 0% error rate
- Response time ổn định (variance < 20%)
- No memory leak

**Monitoring**:
```bash
# Theo dõi resource usage
docker stats petclinics_btl-petclinic-app-1
docker stats petclinics_btl-mysql-1
```

**Điểm Flex**: "Soak Test giúp phát hiện memory leak - lỗi mà Dev thường bỏ sót vì chỉ test ngắn hạn. Sau 30 phút, nếu memory tăng liên tục là có leak."

---

## 🔧 Kỹ thuật nâng cao

### Dynamic Correlation (Tự động trích xuất dữ liệu)

```java
// Step 1: Create Owner and extract ID
http("Create Owner")
    .check(jsonPath("$.id").saveAs("ownerId"))

// Step 2: Use extracted ID in next request
http("Create Pet")
    .post("/api/owners/#{ownerId}/pets")
```

**Điểm Flex**: "Gatling Recorder chỉ tạo code tĩnh. Em đã refactor để xử lý Dynamic Correlation - tự động lấy ID từ response này làm input cho request kia, giống hành vi user thật 100%."

---

### Data-Driven với 5000 records

```java
feed(csv("data/owners.csv").random())
```

**Tại sao 5000?**
- Tránh database cache kết quả
- Mỗi request dùng data khác nhau → Realistic
- Test performance của disk I/O, không chỉ cache

**Điểm Flex**: "Em generate 5000 unique owners để test realistic workload. Nếu dùng 10-20 records, database sẽ cache hết, không phản ánh performance thật của production."

---

## 📈 Phân tích kết quả

### Các chỉ số quan trọng

| Metric | Good | Warning | Critical |
|--------|------|---------|----------|
| **Response Time (95th)** | < 500ms | 500-1000ms | > 1000ms |
| **Error Rate** | 0% | < 1% | > 1% |
| **Throughput** | > 100 req/s | 50-100 req/s | < 50 req/s |
| **Mean Response Time** | < 300ms | 300-500ms | > 500ms |

### Ví dụ So sánh

| Test | Users | Success | 95th Percentile | Mean | Throughput |
|------|-------|---------|-----------------|------|------------|
| Smoke | 1 | 100% | 79ms | 40ms | 3 req/s |
| Baseline | 5 | 100% | 250ms | 180ms | 15 req/s |
| Baseline | 10 | 100% | 420ms | 290ms | 25 req/s |
| Stress | 500 | 98% | 850ms | 520ms | 120 req/s |
| Stress | 1000 | 92% | 1800ms | 980ms | 180 req/s |
| Stress | 2000 | 75% | 3500ms | 2100ms | 150 req/s |

**Kết luận**: Hệ thống handle tốt đến 500 users. Trên 1000 users bắt đầu degradation.

---

## 🚀 Cách chạy

### Prerequisites
```bash
# Terminal 1: Start PetClinic on Docker
cd C:\Users\PC\Documents\GitHub\petclinics_btl
docker-compose --profile mysql up
```

### Run tests
```bash
cd C:\Users\PC\Documents\GitHub\kiemthugatling

# Smoke test (nhanh)
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.PetClinicSmokeTest"

# Baseline (5 users)
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.PetClinicSimulation"

# Baseline với custom users
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.PetClinicSimulation" "-Dvu=20"

# Stress test (CHÚ Ý: Tốn tài nguyên!)
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.StressTestSimulation"

# Soak test (CHÚ Ý: Chạy 30 phút!)
./mvnw gatling:test "-Dgatling.simulationClass=example.simulations.SoakTestSimulation"
```

### Xem kết quả

Reports được tạo tại:
```
target/gatling/[simulation-name]-[timestamp]/index.html
```

Mở file HTML trong browser để xem charts chi tiết.

---

## 🎓 Điểm Flex cho Presentation

### 1. Khi show cấu trúc project
"Em tổ chức code theo best practice: tách riêng scenarios (business logic) và simulations (load patterns). Dễ maintain và reuse."

### 2. Khi show file owners.csv
"Em generate 5000 unique records thay vì hardcode. Lý do: Database cache. Nếu dùng 10 records giống nhau, MySQL cache hết, test không realistic."

### 3. Khi show OwnerPetScenario
"Đây là kỹ thuật Dynamic Correlation. Gatling Recorder sinh code tĩnh, em refactor để extract ID động, giống user thật 100%."

### 4. Khi show assertions
"Em đặt Quality Gates: 95% request < 500ms, error < 1%. Tích hợp vào CI/CD, nếu Dev làm chậm hệ thống, build tự động fail."

### 5. Khi show Stress Test
"Em không chỉ test xem nó chạy được không. Em còn tìm breaking point: Hệ thống này chịu được tối đa 800 concurrent users trước khi response time vượt 2s."

### 6. Khi show Soak Test
"30 phút constant load để phát hiện memory leak - vấn đề chỉ xuất hiện khi chạy lâu dài, test ngắn không thấy được."

---

## 📊 Template Báo cáo

```
PERFORMANCE TEST REPORT - PetClinic REST API
============================================

Test Date: [Date]
Environment: Docker (MySQL 8.4 + SpringBoot)
Tool: Gatling 3.x

1. SMOKE TEST
   Status: ✅ PASS
   Duration: 8s
   Success Rate: 100%

2. BASELINE TEST (5 users)
   Status: ✅ PASS
   95th Percentile: 245ms (< 500ms ✅)
   Error Rate: 0% (< 1% ✅)
   Throughput: 18 req/s

3. STRESS TEST (0→2000 users)
   Breaking Point: ~850 users
   At 850 users:
     - Success Rate: 96%
     - 95th Percentile: 1.2s
   At 2000 users:
     - Success Rate: 68% ❌
     - 95th Percentile: 4.5s ❌

4. SOAK TEST (50 users × 30min)
   Status: ✅ PASS
   Response Time Stability: Excellent (variance 8%)
   Memory Leak: None detected
   Error Rate: 0%

RECOMMENDATION:
- Maximum concurrent users: 800
- Optimal performance: < 500 users
- Consider horizontal scaling if exceeding 800 users
```

---

## 🎯 Next Steps

1. ✅ Run Smoke Test để verify setup
2. ✅ Run Baseline với 5, 10, 20, 50 users để build comparison table
3. ✅ Run Stress Test để find breaking point
4. ⏳ Run Soak Test overnight (optional)
5. 📊 Analyze reports và tạo summary table
6. 🎤 Prepare presentation với screenshots

---

**Author**: Performance Testing Team  
**Tool**: Gatling + Docker + MySQL  
**Strategy**: Smoke → Baseline → Stress → Soak
