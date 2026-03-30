# Lab 7 – Kiểm thử dự án PolyCoffee

---

## Bài 1: Phân tích và xác định phạm vi kiểm thử (3 điểm)

### 1.1 Các chức năng cần kiểm thử
- Đăng nhập / Đăng xuất
- Quản lý loại đồ uống (CRUD)
- Quản lý đồ uống (CRUD + tìm kiếm + phân trang)
- Quản lý nhân viên (CRUD + cấp lại mật khẩu)
- Quản lý hóa đơn (POS: tạo, thêm thức uống, cập nhật số lượng, thanh toán, hủy)
- Thống kê (Top 5 bán chạy, doanh thu)
- Phân quyền (AuthFilter: nhân viên vs quản lý)

### 1.2 Các chức năng KHÔNG nằm trong phạm vi kiểm thử
- Giao diện responsive trên mobile
- Hiệu năng tải nặng (load testing)
- Tích hợp thanh toán online
- Triển khai lên server production

### 1.3 Mục tiêu kiểm thử
- Đảm bảo tất cả chức năng CRUD hoạt động đúng
- Xác minh phân quyền hoạt động chính xác
- Kiểm tra validate dữ liệu phía server
- Đảm bảo luồng POS bán hàng hoạt động trơn tru

### 1.4 Rủi ro kiểm thử
1. **Dữ liệu test không đầy đủ**: Thiếu dữ liệu mẫu dẫn đến không kiểm tra được các trường hợp biên → Chuẩn bị script SQL tạo dữ liệu test
2. **Thay đổi yêu cầu muộn**: Nghiệp vụ POS có thể thay đổi trong quá trình phát triển → Cập nhật test case kịp thời
3. **Môi trường test không ổn định**: Kết nối SQL Server bị gián đoạn → Kiểm tra connection trước mỗi đợt test

---

## Bài 2: Kế hoạch kiểm thử – Test Plan (2 điểm)

### 2.1 Phạm vi kiểm thử
- Chức năng quản lý hóa đơn (POS): Tạo đơn, thêm thức uống, cập nhật số lượng, thanh toán, hủy đơn

### 2.2 Phương pháp tiếp cận
- **Manual Testing**: Kiểm thử thủ công trên trình duyệt
- Kỹ thuật: Black-box testing (kiểm thử hộp đen dựa trên yêu cầu chức năng)
- Mức độ bao phủ: Kiểm thử tất cả các luồng chính và ngoại lệ

### 2.3 Chiến lược kiểm thử
- Ưu tiên kiểm thử chức năng tạo và thanh toán hóa đơn (nghiệp vụ quan trọng nhất)
- Kiểm thử phân quyền truy cập POS
- Kiểm thử các trường hợp biên (số lượng = 0, đơn hàng rỗng)

### 2.4 Nguồn lực kiểm thử
- **Nhân sự**: 1-2 thành viên nhóm
- **Môi trường**: Windows + IntelliJ IDEA + Tomcat 9 + SQL Server
- **Công cụ**: Trình duyệt Chrome, SQL Server Management Studio
- **Dữ liệu**: Script SQL tạo dữ liệu mẫu

### 2.5 Tiêu chí kết thúc kiểm thử
- 100% test case được thực hiện
- Tất cả lỗi mức Critical và High đã được sửa
- Tỷ lệ Pass >= 90%

---

## Bài 3: Test Cases – Chức năng tạo và xử lý hóa đơn (2 điểm)

### TC-001: Tạo đơn hàng mới thành công
- **Test Case ID**: TC-001
- **Unit to Test**: POST /employee/pos/init
- **Test Data**: drinkId = 1, price = 25000 (Cà phê đen)
- **Steps to Execute**:
  1. Đăng nhập bằng tài khoản nhân viên
  2. Truy cập trang POS (/employee/pos)
  3. Nhấn nút "Thêm" ở thức uống Cà phê đen
- **Expected Result**: Đơn hàng mới được tạo với trạng thái "waiting", thức uống Cà phê đen xuất hiện trong đơn với số lượng = 1

### TC-002: Thêm thức uống vào đơn hàng đang có
- **Test Case ID**: TC-002
- **Unit to Test**: POST /employee/pos/init (đơn đã tồn tại)
- **Test Data**: drinkId = 2, price = 30000 (Cà phê sữa), đơn hàng đang waiting
- **Steps to Execute**:
  1. Đã có đơn hàng đang chờ (từ TC-001)
  2. Nhấn nút "Thêm" ở thức uống Cà phê sữa
- **Expected Result**: Cà phê sữa được thêm vào đơn hàng hiện tại, tổng tiền được cập nhật = 55000

### TC-003: Tăng số lượng thức uống
- **Test Case ID**: TC-003
- **Unit to Test**: POST /employee/pos/update-quantity (action=increase)
- **Test Data**: detailId = 1, quantityAction = "increase"
- **Steps to Execute**:
  1. Trong đơn hàng đang chờ, nhấn nút "+" ở Cà phê đen
- **Expected Result**: Số lượng Cà phê đen tăng từ 1 lên 2, tổng tiền được cập nhật

### TC-004: Thanh toán đơn hàng thành công
- **Test Case ID**: TC-004
- **Unit to Test**: POST /employee/pos/checkout
- **Test Data**: billId = đơn hàng đang waiting
- **Steps to Execute**:
  1. Nhấn nút "Thanh toán" trên đơn hàng
- **Expected Result**: Trạng thái đơn chuyển thành "finish", hiển thị thông báo "Thanh toán thành công!", trang POS trở về trạng thái trống

### TC-005: Giảm số lượng về 0 sẽ xóa thức uống
- **Test Case ID**: TC-005
- **Unit to Test**: POST /employee/pos/update-quantity (action=decrease)
- **Test Data**: detailId với quantity = 1, quantityAction = "decrease"
- **Steps to Execute**:
  1. Tạo đơn hàng mới với 1 thức uống (quantity = 1)
  2. Nhấn nút "-" ở thức uống đó
- **Expected Result**: Thức uống bị xóa khỏi đơn hàng (quantity giảm về 0 → xóa)

### TC-006: Hủy đơn hàng
- **Test Case ID**: TC-006
- **Unit to Test**: POST /employee/pos/cancel
- **Test Data**: billId = đơn hàng đang waiting
- **Steps to Execute**:
  1. Tạo đơn hàng mới, thêm thức uống
  2. Nhấn nút "Hủy đơn"
- **Expected Result**: Trạng thái đơn chuyển thành "cancel", thông báo "Đã hủy đơn hàng!"

### TC-007: Nhân viên không truy cập được trang quản lý
- **Test Case ID**: TC-007
- **Unit to Test**: AuthFilter - phân quyền
- **Test Data**: Tài khoản nhân viên (role = false)
- **Steps to Execute**:
  1. Đăng nhập bằng tài khoản nhân viên
  2. Truy cập URL /manager/categories
- **Expected Result**: Redirect về trang đăng nhập (không được phép truy cập)

---

## Bài 4: Thực hiện kiểm thử và ghi nhận lỗi (2 điểm)

### Kết quả thực hiện kiểm thử

| TC-ID  | Kết quả | Ghi chú          |
|--------|---------|------------------|
| TC-001 | Pass    |                  |
| TC-002 | Pass    |                  |
| TC-003 | Pass    |                  |
| TC-004 | Pass    |                  |
| TC-005 | Fail    | Xem BUG-001      |
| TC-006 | Pass    |                  |
| TC-007 | Fail    | Xem BUG-002      |

### BUG-001: Giảm số lượng không cập nhật lại tổng tiền
- **Mô tả lỗi**: Khi giảm số lượng thức uống về 0 (xóa), tổng tiền đơn hàng không được cập nhật lại
- **Steps to Reproduce**:
  1. Tạo đơn hàng, thêm 1 thức uống (25.000đ)
  2. Nhấn nút "-" để giảm số lượng về 0
  3. Thức uống bị xóa nhưng tổng tiền vẫn hiện 25.000đ
- **Actual Result**: Tổng tiền vẫn = 25.000đ sau khi xóa thức uống
- **Expected Result**: Tổng tiền = 0đ
- **Severity**: High
- **Vòng đời lỗi**:
  - New → Assigned to Developer → In Progress → Fixed (thêm gọi billDAO.updateTotal sau khi xóa) → Retest → Pass → Closed

### BUG-002: Nhân viên vẫn truy cập được trang quản lý khi nhập URL trực tiếp
- **Mô tả lỗi**: AuthFilter không chặn request tới /manager/* khi user có role = false
- **Steps to Reproduce**:
  1. Đăng nhập bằng tài khoản nhân viên (role = false)
  2. Nhập trực tiếp URL: http://localhost:8080/manager/categories
  3. Trang quản lý hiển thị bình thường
- **Actual Result**: Nhân viên truy cập được trang quản lý
- **Expected Result**: Redirect về trang đăng nhập
- **Severity**: Critical
- **Vòng đời lỗi**:
  - New → Assigned → In Progress → Fixed (sửa logic kiểm tra role trong AuthFilter) → Retest → Pass → Closed
