-- =============================================
-- Lab 3 - Bài 1: Thiết kế & tạo CSDL PolyCoffee
-- =============================================

CREATE DATABASE PolyCoffee;
GO

USE PolyCoffee;
GO

-- =============================================
-- Bảng loại đồ uống
-- =============================================
CREATE TABLE categories (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    name        NVARCHAR(100) NOT NULL,
    active      BIT NOT NULL DEFAULT 1
);
GO

-- =============================================
-- Bảng đồ uống
-- =============================================
CREATE TABLE drinks (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    category_id INT NOT NULL,
    name        NVARCHAR(200) NOT NULL,
    price       FLOAT NOT NULL DEFAULT 0,
    image       NVARCHAR(255),
    description NVARCHAR(500),
    active      BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_drinks_categories FOREIGN KEY (category_id) REFERENCES categories(id)
);
GO

-- =============================================
-- Bảng người dùng
-- =============================================
CREATE TABLE users (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    email       NVARCHAR(150) NOT NULL UNIQUE,
    password    NVARCHAR(255) NOT NULL,
    full_name   NVARCHAR(150) NOT NULL,
    phone       NVARCHAR(20),
    role        BIT NOT NULL DEFAULT 0,   -- 0: Nhân viên, 1: Quản lý
    active      BIT NOT NULL DEFAULT 1
);
GO

-- =============================================
-- Bảng thẻ
-- =============================================
CREATE TABLE cards (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    code        NVARCHAR(50) NOT NULL,
    status      BIT NOT NULL DEFAULT 1
);
GO

-- =============================================
-- Bảng hóa đơn
-- =============================================
CREATE TABLE bills (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    user_id     INT NOT NULL,
    card_id     INT,
    code        NVARCHAR(50) NOT NULL,
    created_at  DATETIME NOT NULL DEFAULT GETDATE(),
    total       FLOAT NOT NULL DEFAULT 0,
    status      NVARCHAR(20) NOT NULL DEFAULT 'waiting',
    CONSTRAINT FK_bills_users FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT FK_bills_cards FOREIGN KEY (card_id) REFERENCES cards(id)
);
GO

-- =============================================
-- Bảng chi tiết hóa đơn
-- =============================================
CREATE TABLE bill_details (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    bill_id     INT NOT NULL,
    drink_id    INT NOT NULL,
    quantity    INT NOT NULL DEFAULT 1,
    price       FLOAT NOT NULL DEFAULT 0,
    CONSTRAINT FK_bill_details_bills FOREIGN KEY (bill_id) REFERENCES bills(id),
    CONSTRAINT FK_bill_details_drinks FOREIGN KEY (drink_id) REFERENCES drinks(id)
);
GO

-- =============================================
-- Dữ liệu mẫu
-- =============================================

-- Loại đồ uống
INSERT INTO categories (name, active) VALUES (N'Cà phê', 1);
INSERT INTO categories (name, active) VALUES (N'Trà', 1);
INSERT INTO categories (name, active) VALUES (N'Sinh tố', 1);
INSERT INTO categories (name, active) VALUES (N'Nước ép', 0);
GO

-- Người dùng
INSERT INTO users (email, password, full_name, phone, role, active)
VALUES (N'admin@polycoffee.com', N'123456', N'Nguyễn Văn Quản Lý', N'0901234567', 1, 1);

INSERT INTO users (email, password, full_name, phone, role, active)
VALUES (N'nhanvien01@polycoffee.com', N'123456', N'Trần Thị Nhân Viên', N'0907654321', 0, 1);

INSERT INTO users (email, password, full_name, phone, role, active)
VALUES (N'nhanvien02@polycoffee.com', N'123456', N'Lê Văn Phục Vụ', N'0912345678', 0, 1);
GO

-- Đồ uống
INSERT INTO drinks (category_id, name, price, image, description, active)
VALUES (1, N'Cà phê đen', 25000, N'caphe_den.jpg', N'Cà phê đen truyền thống', 1);

INSERT INTO drinks (category_id, name, price, image, description, active)
VALUES (1, N'Cà phê sữa', 30000, N'caphe_sua.jpg', N'Cà phê sữa đá', 1);

INSERT INTO drinks (category_id, name, price, image, description, active)
VALUES (2, N'Trà đào', 35000, N'tra_dao.jpg', N'Trà đào cam sả', 1);

INSERT INTO drinks (category_id, name, price, image, description, active)
VALUES (2, N'Trà sữa', 30000, N'tra_sua.jpg', N'Trà sữa trân châu', 1);

INSERT INTO drinks (category_id, name, price, image, description, active)
VALUES (3, N'Sinh tố bơ', 40000, N'sinhto_bo.jpg', N'Sinh tố bơ tươi', 1);

INSERT INTO drinks (category_id, name, price, image, description, active)
VALUES (3, N'Sinh tố dâu', 38000, N'sinhto_dau.jpg', N'Sinh tố dâu tây', 1);
GO

-- Thẻ
INSERT INTO cards (code, status) VALUES (N'CARD001', 1);
INSERT INTO cards (code, status) VALUES (N'CARD002', 1);
GO

-- Hóa đơn
INSERT INTO bills (user_id, card_id, code, created_at, total, status)
VALUES (2, NULL, N'HD001', GETDATE(), 55000, N'finish');

INSERT INTO bills (user_id, card_id, code, created_at, total, status)
VALUES (2, 1, N'HD002', GETDATE(), 70000, N'waiting');
GO

-- Chi tiết hóa đơn
INSERT INTO bill_details (bill_id, drink_id, quantity, price) VALUES (1, 1, 1, 25000);
INSERT INTO bill_details (bill_id, drink_id, quantity, price) VALUES (1, 2, 1, 30000);
INSERT INTO bill_details (bill_id, drink_id, quantity, price) VALUES (2, 3, 2, 35000);
GO
