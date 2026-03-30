# Lab 8 – Tài liệu và triển khai dự án PolyCoffee

---

## Bài 1: Phân tích và lập danh mục tài liệu kỹ thuật (3 điểm)

### 1.1 Đối tượng đọc tài liệu
- **Lập trình viên mới**: Hiểu kiến trúc hệ thống, cách tổ chức source code để tiếp tục phát triển
- **Người triển khai**: Biết cách cài đặt môi trường, cấu hình server, database
- **Giảng viên / Người đánh giá**: Hiểu tổng quan dự án, công nghệ, cách thực hiện

### 1.2 Các loại tài liệu kỹ thuật cần có

**1. Tài liệu tổng quan hệ thống**
- Mục tiêu: Mô tả mục tiêu dự án, phạm vi chức năng, đối tượng sử dụng
- Giúp người đọc hình dung toàn bộ hệ thống từ góc nhìn tổng quan

**2. Tài liệu kiến trúc**
- Mục tiêu: Trình bày mô hình Client-Server, MVC pattern, mối quan hệ giữa các thành phần
- Giúp developer hiểu cách hệ thống được tổ chức

**3. Tài liệu cài đặt & triển khai**
- Mục tiêu: Hướng dẫn cài đặt môi trường, deploy ứng dụng
- Giúp bất kỳ ai cũng có thể chạy được dự án

---

## Bài 2: Giới thiệu dự án và kiến trúc hệ thống (2 điểm)

### 2.1 Giới thiệu dự án

- **Tên dự án**: PolyCoffee – Hệ thống quản lý bán đồ uống
- **Mục tiêu nghiệp vụ**: Xây dựng website giúp quán coffee Fpoly quản lý bán hàng, quản lý nhân viên, thống kê doanh thu một cách nhanh chóng và thuận tiện
- **Phạm vi chức năng chính**:
  - Đăng nhập / phân quyền (Quản lý, Nhân viên)
  - Quản lý loại đồ uống, đồ uống (CRUD)
  - Quản lý nhân viên (CRUD + cấp lại mật khẩu)
  - POS bán hàng (tạo hóa đơn, thanh toán, hủy)
  - Thống kê doanh thu, sản phẩm bán chạy
- **Đối tượng sử dụng**:
  - Quản lý: Toàn quyền quản lý hệ thống
  - Nhân viên: Sử dụng POS bán hàng, xem hóa đơn
- **Luồng hoạt động tổng quan**:
  Người dùng → Đăng nhập → Phân quyền → Chức năng tương ứng → Thao tác CSDL → Hiển thị kết quả

### 2.2 Kiến trúc hệ thống

**Mô hình Client – Server**
- Client (Browser): Gửi HTTP Request, nhận HTML/CSS/JS response
- Server (Tomcat): Xử lý business logic, điều phối request/response
- Database (SQL Server): Lưu trữ và truy xuất dữ liệu

**Mô hình MVC**
- **Model**: Entity classes (Category, Drink, User, Bill, BillDetail) + DAO classes
- **View**: JSP pages (sử dụng JSTL, Bootstrap)
- **Controller**: Servlet classes (AuthServlet, CategoryServlet, DrinkServlet, PosServlet...)

**Nguyên tắc thiết kế**:
- Phân tách rõ trách nhiệm giữa Model, View, Controller
- DAO pattern để tách biệt logic truy xuất dữ liệu
- Filter pattern cho xác thực và phân quyền
- Utility classes cho các tác vụ dùng chung

---

## Bài 3: Công nghệ sử dụng và cấu trúc source code (2 điểm)

### 3.1 Công nghệ sử dụng

- **Ngôn ngữ**: Java (JDK 21)
- **Công nghệ Web**: Java Servlet, JSP, JSTL
- **Application Server**: Apache Tomcat 9
- **Cơ sở dữ liệu**: Microsoft SQL Server
- **Kết nối DB**: JDBC (mssql-jdbc driver)
- **Giao diện**: Bootstrap 5, Chart.js (biểu đồ thống kê)
- **IDE**: IntelliJ IDEA
- **Thư viện bổ sung**: JavaMail (gửi email), JSTL 1.2

### 3.2 Cấu trúc source code theo MVC

```
src/main/java/
├── entity/          ← Model: Các lớp thực thể
│   ├── Category.java
│   ├── Drink.java
│   ├── User.java
│   ├── Card.java
│   ├── Bill.java
│   ├── BillDetail.java
│   ├── Revenue.java
│   └── BestSellingDrink.java
├── dao/             ← Model: Truy xuất dữ liệu
│   ├── CrudDAO.java          (Interface chung)
│   ├── CategoryDAO.java
│   ├── DrinkDAO.java
│   ├── UserDAO.java
│   ├── BillDAO.java
│   ├── BillDetailDAO.java
│   ├── CardDAO.java
│   └── StatisticDAO.java
├── servlet/         ← Controller: Điều phối request
│   ├── AuthServlet.java
│   ├── CategoryServlet.java
│   ├── DrinkServlet.java
│   ├── StaffServlet.java
│   ├── PosServlet.java
│   └── StatisticServlet.java
├── filter/          ← Filter: Xử lý trước/sau request
│   ├── Utf8Filter.java
│   └── AuthFilter.java
└── utils/           ← Utility: Lớp tiện ích
    ├── JdbcUtil.java
    ├── AuthUtil.java
    ├── ParamUtil.java
    ├── FileUtil.java
    └── EmailUtil.java

src/main/webapp/
├── WEB-INF/
│   └── views/       ← View: Giao diện JSP
│       ├── auth/login.jsp
│       ├── employee/pos.jsp
│       └── manager/
│           ├── category/  (list.jsp, form.jsp)
│           ├── drink/     (list.jsp, form.jsp)
│           ├── staff/     (list.jsp, form.jsp)
│           ├── bill/      (list.jsp, detail.jsp)
│           └── statistic/ (top-drinks.jsp, revenue.jsp)
└── uploads/          ← Thư mục upload hình ảnh
```

### 3.3 Quy ước code
- Đặt tên class theo PascalCase: `CategoryDAO`, `AuthServlet`
- Đặt tên method theo camelCase: `findById()`, `updateStatus()`
- Đặt tên biến rõ nghĩa: `billId`, `totalRevenue`
- Không viết logic nghiệp vụ trong JSP (chỉ hiển thị dữ liệu)
- Sử dụng PreparedStatement để tránh SQL Injection

---

## Bài 4: Hướng dẫn cài đặt (2 điểm)

### 4.1 Yêu cầu phần mềm
- JDK 21
- IntelliJ IDEA (Community hoặc Ultimate)
- Apache Tomcat 9.x
- Microsoft SQL Server 2016+
- SQL Server Management Studio (SSMS)

### 4.2 Các bước cài đặt và chạy project trên máy local

**Bước 1: Cài đặt cơ sở dữ liệu**
1. Mở SSMS, kết nối đến SQL Server
2. Mở file `PolyCoffee.sql`
3. Thực thi toàn bộ script để tạo database, bảng, và dữ liệu mẫu
4. Thực thi file `procedures.sql` để tạo stored procedures thống kê

**Bước 2: Cấu hình project**
1. Mở IntelliJ IDEA, import project
2. Mở file `JdbcUtil.java`, cập nhật thông tin kết nối:
   - URL: `jdbc:sqlserver://localhost:1433;databaseName=PolyCoffee;encrypt=false`
   - USER: `sa`
   - PASSWORD: (mật khẩu SQL Server của bạn)
3. Thêm các thư viện cần thiết vào `WEB-INF/lib/`:
   - `mssql-jdbc-12.x.x.jre11.jar` (JDBC driver)
   - `javax.servlet.jsp.jstl-1.2.1.jar` (JSTL)
   - `javax.servlet.jsp.jstl-api-1.2.1.jar`
   - `javax.mail-1.6.2.jar` (JavaMail - nếu dùng chức năng gửi email)

**Bước 3: Cấu hình Tomcat trong IntelliJ**
1. Run → Edit Configurations → Add → Tomcat Server → Local
2. Chọn thư mục cài đặt Tomcat 9
3. Tab Deployment → Add Artifact → chọn war exploded
4. Application context: `/polycoffee`

**Bước 4: Chạy ứng dụng**
1. Nhấn Run (Shift + F10)
2. Truy cập: `http://localhost:8080/polycoffee/auth/login`
3. Đăng nhập:
   - Quản lý: `admin@polycoffee.com` / `123456`
   - Nhân viên: `nhanvien01@polycoffee.com` / `123456`

### 4.3 Kiểm tra ứng dụng chạy thành công
- Trang đăng nhập hiển thị đúng → OK
- Đăng nhập quản lý → Redirect tới trang quản lý loại đồ uống → OK
- Đăng nhập nhân viên → Redirect tới trang POS → OK
- CRUD loại đồ uống hoạt động → OK
- Thống kê hiển thị biểu đồ → OK
