# 🎯 DANH SÁCH 14 KỊCH BẢN GATLING CHO PETCLINIC

Tổng số: **14 kịch bản hoàn chỉnh** (4 kịch bản cũ + 10 kịch bản mới)

---

## 📋 KỊCH BẢN CŨ (Đã có sẵn)

### 1. **ClinicalWorkflowTest** - Quy trình khám bệnh hoàn chỉnh
- **Câu hỏi:** "Chủ nuôi vào hệ thống để hoàn thành quy trình khám bệnh từ đầu đến cuối"
- **Mở bài:** Tạo owner
- **Thân bài:** Tạo pet → Xem vets → Đặt visit → Cập nhật visit
- **Kết bài:** Xác nhận visit
- **File:** `ClinicalWorkflowTest.java`

### 2. **Emergency Visit Workflow** - Khám khẩn cấp
- **Câu hỏi:** "Thú cưng gặp tình huống khẩn cấp cần khám ngay"
- **File:** `ClinicalWorkflowTest.java` (scenario thứ 2)

### 3. **Routine Checkup Workflow** - Khám định kỳ
- **Câu hỏi:** "Đặt lịch khám định kỳ cho thú cưng"
- **File:** `ClinicalWorkflowTest.java` (scenario thứ 3)

### 4. **Admin Reference Data Management** - Quản lý dữ liệu tham chiếu
- **Câu hỏi:** "Admin cấu hình hệ thống: pet types, specialties, vets"
- **File:** `ClinicalWorkflowTest.java` (scenario thứ 4)

---

## 🆕 KỊCH BẢN MỚI (Vừa tạo)

### NHÓM A: KỊCH BẢN KHÁCH HÀNG (Customer Journey)

#### 5. **NewPatientRegistrationJourney** 🎯
- **Câu hỏi:** "Chủ nuôi mới lần đầu tiên đăng ký thông tin và đặt lịch khám cho thú cưng"
- **Mở bài:** Tìm hiểu về phòng khám (xem vets, specialties, pet types)
- **Thân bài:** Đăng ký owner → Thêm pet → Chọn bác sĩ → Đặt lịch khám
- **Kết bài:** Xác nhận thông tin, kiểm tra lịch hẹn
- **Users:** 10 concurrent | Duration: 5 min
- **File:** `NewPatientRegistrationJourney.java`

#### 6. **ExistingOwnerAddPetJourney** 🐕➕🐈
- **Câu hỏi:** "Chủ nuôi hiện tại vừa mua thêm thú cưng mới, muốn thêm vào hệ thống và đặt lịch tiêm phòng"
- **Mở bài:** Đăng nhập, xem thông tin tài khoản và pets hiện tại
- **Thân bài:** Kiểm tra pet types → Thêm pet mới → Chọn bác sĩ → Đặt lịch tiêm phòng
- **Kết bài:** Xác nhận pet mới và lịch tiêm phòng
- **Users:** 8 concurrent | Duration: 6 min
- **File:** `ExistingOwnerAddPetJourney.java`

#### 7. **RescheduleVisitJourney** 📅
- **Câu hỏi:** "Chủ nuôi có việc bận, cần đổi lịch hẹn khám bệnh sang ngày khác"
- **Mở bài:** Đăng nhập, xem danh sách lịch hẹn hiện tại
- **Thân bài:** Xem chi tiết lịch cũ → Cập nhật thời gian mới → Cập nhật lý do khám
- **Kết bài:** Xác nhận lịch mới, xem tất cả lịch hẹn
- **Users:** 6 concurrent | Duration: 4 min
- **File:** `RescheduleVisitJourney.java`

---

### NHÓM B: KỊCH BẢN QUẢN TRỊ VIÊN (Admin Journey)

#### 8. **OnboardVeterinarianJourney** 👨‍⚕️
- **Câu hỏi:** "Admin tuyển bác sĩ mới, cần thêm vào hệ thống và gán chuyên khoa"
- **Mở bài:** Xem danh sách vets hiện tại, xem specialties có sẵn
- **Thân bài:** Tạo vet mới → Gán chuyên khoa → Cập nhật thông tin bổ sung
- **Kết bài:** Xác nhận vet mới, kiểm tra danh sách
- **Users:** 3 concurrent | Duration: 3 min
- **File:** `OnboardVeterinarianJourney.java`

#### 9. **ExpandSpecialtiesJourney** 🏥
- **Câu hỏi:** "Admin mở thêm chuyên khoa mới (ví dụ: Chỉnh hình, Tim mạch) và gán bác sĩ phụ trách"
- **Mở bài:** Phân tích nhu cầu, xem specialties và vets hiện tại
- **Thân bài:** Tạo specialty mới → Cập nhật mô tả → Tạo/gán vet cho specialty
- **Kết bài:** Xác nhận specialty đã active
- **Users:** 2 concurrent | Duration: 4 min
- **File:** `ExpandSpecialtiesJourney.java`

#### 10. **ExpandPetTypesJourney** 🦎
- **Câu hỏi:** "Admin mở rộng dịch vụ, thêm hỗ trợ cho loại thú cưng mới (bò sát, chim, thỏ...)"
- **Mở bài:** Xem danh sách pet types hiện tại
- **Thân bài:** Tạo pet type mới → Cập nhật mô tả → Test bằng owner/pet mẫu → Xóa test data
- **Kết bài:** Xác nhận pet type mới hoạt động
- **Users:** 2 concurrent | Duration: 3 min
- **File:** `ExpandPetTypesJourney.java`

---

### NHÓM C: KỊCH BẢN KHẨN CẤP (Emergency Journey)

#### 11. **EmergencyVisitJourney** 🚨
- **Câu hỏi:** "Thú cưng gặp tai nạn/bệnh nặng, chủ nuôi cần đặt lịch khám khẩn cấp ngay lập tức"
- **Mở bài:** Tình huống khẩn cấp, xem bác sĩ khả dụng NGAY
- **Thân bài:** Đăng ký nhanh → Thêm pet tối thiểu → Đặt lịch EMERGENCY (không pause lâu)
- **Kết bài:** Nhận xác nhận, xem thông tin bác sĩ và địa chỉ
- **Users:** 5 concurrent (burst load) | Duration: 2 min
- **File:** `EmergencyVisitJourney.java`

#### 12. **WalkInVisitJourney** 🚶
- **Câu hỏi:** "Chủ nuôi đã ở phòng khám, lễ tân cần đăng ký thông tin nhanh cho khách walk-in"
- **Mở bài:** Khách walk-in đến, lễ tân kiểm tra khách cũ/mới, xem bác sĩ rảnh
- **Thân bài:** Tìm/tạo owner → Thêm pet → Tạo visit walk-in → Ghi chú "No appointment"
- **Kết bài:** In phiếu khám, hướng dẫn khách chờ
- **Users:** 8 concurrent (random spikes) | Duration: 3 min
- **File:** `WalkInVisitJourney.java`

---

### NHÓM D: KỊCH BẢN PHỨC TẠP (Complex Journey)

#### 13. **MultiPetManagementJourney** 🐕🐈🐇
- **Câu hỏi:** "Chủ nuôi có 3 con thú cưng, cần đặt lịch khám định kỳ cho cả 3 con cùng ngày"
- **Mở bài:** Đăng nhập, xem danh sách tất cả pets, xem lịch sử khám
- **Thân bài:** Thêm 3 pets → Cập nhật thông tin từng con → Đặt lịch cho cả 3 (cùng ngày)
- **Kết bài:** Xem tất cả lịch hẹn, in tổng hợp
- **Users:** 5 concurrent | Duration: 7 min
- **File:** `MultiPetManagementJourney.java`

#### 14. **CompletePatientLifecycleJourney** 🔄
- **Câu hỏi:** "Mô phỏng toàn bộ vòng đời: Đăng ký → Khám → Tái khám → Đổi lịch → Cập nhật → Chuyển pet → Xóa"
- **Mở bài:** Tìm hiểu hệ thống, đăng ký tài khoản
- **Thân bài:** 
  - Phase 1: Đăng ký và khám lần đầu
  - Phase 2: Tái khám và cập nhật
  - Phase 3: Đổi lịch và quản lý
  - Phase 4: Chuyển nhượng pet
  - Phase 5: Dọn dẹp dữ liệu
- **Kết bài:** Xác nhận vòng đời hoàn tất
- **Users:** 3 concurrent | Duration: 10 min
- **File:** `CompletePatientLifecycleJourney.java`

---

## 🚀 CÁCH CHẠY

```bash
# Chạy từng kịch bản cụ thể
mvn gatling:test -Dgatling.simulationClass=example.simulations.NewPatientRegistrationJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.ExistingOwnerAddPetJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.RescheduleVisitJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.OnboardVeterinarianJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.ExpandSpecialtiesJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.ExpandPetTypesJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.EmergencyVisitJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.WalkInVisitJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.MultiPetManagementJourney
mvn gatling:test -Dgatling.simulationClass=example.simulations.CompletePatientLifecycleJourney

# Hoặc chạy interactive để chọn
mvn gatling:test
```

---

## 📊 PHÂN LOẠI KỊCH BẢN

| Loại | Số lượng | Mục đích |
|------|----------|----------|
| **Customer Journey** | 7 kịch bản | Mô phỏng hành vi khách hàng thực tế |
| **Admin Journey** | 4 kịch bản | Mô phỏng quản trị viên cấu hình hệ thống |
| **Emergency Journey** | 2 kịch bản | Kiểm tra hiệu năng khi tải đột biến |
| **Complex Journey** | 2 kịch bản | Kiểm tra kịch bản phức tạp nhiều bước |

---

## ✅ ĐẶC ĐIỂM CỦA 10 KỊCH BẢN MỚI

✔️ **Đầy đủ cấu trúc 3 phần:** Mở bài → Thân bài → Kết bài  
✔️ **Trả lời câu hỏi:** "Người dùng vào hệ thống để làm xong việc gì?"  
✔️ **Có logging rõ ràng:** Init, Action, Teardown, Complete  
✔️ **Có pause hợp lý:** Mô phỏng thời gian suy nghĩ của người dùng thực  
✔️ **Có validation:** Kiểm tra kết quả sau mỗi bước quan trọng  
✔️ **Đa dạng load pattern:** Ramp, constant, burst, low load  
✔️ **Phản ánh nghiệp vụ thực tế:** Dựa trên use case thực của phòng khám thú y  

---

**Tổng cộng: 14 kịch bản hoàn chỉnh cho dự án Gatling của bạn! 🎉**
