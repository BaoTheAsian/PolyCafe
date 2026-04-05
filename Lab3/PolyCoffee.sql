-- =============================================
-- PolyCoffee — Full Schema (Updated)
-- Changes from original:
--   • users.role   : BIT  → VARCHAR(30)  (supports manager/cashier/barista/staff)
--   • bills         : +payment_method, +table_id
--   • bill_details  : +size, +note, +drink_name (computed column via view)
--   • NEW: cafe_tables
--   • NEW: customers  (loyalty card owners)
--   • cards          : +customer_id FK → customers
--   • NEW: ingredients, drink_ingredients (inventory)
--   • NEW: stock_movements
-- =============================================

CREATE DATABASE PolyCoffee;
GO
USE PolyCoffee;
GO

-- ─── categories ──────────────────────────────────────────────────────────────
CREATE TABLE categories (
    id     INT IDENTITY(1,1) PRIMARY KEY,
    name   NVARCHAR(100) NOT NULL,
    active BIT NOT NULL DEFAULT 1
);
GO

-- ─── drinks ──────────────────────────────────────────────────────────────────
CREATE TABLE drinks (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    category_id INT NOT NULL,
    name        NVARCHAR(200) NOT NULL,
    price       DECIMAL(15,2) NOT NULL DEFAULT 0,
    image       NVARCHAR(255),
    description NVARCHAR(500),
    active      BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_drinks_categories FOREIGN KEY (category_id) REFERENCES categories(id)
);
GO

-- ─── users ───────────────────────────────────────────────────────────────────
-- role is now a VARCHAR so new roles (cashier, barista, supervisor…) need
-- no schema change — just add a new value.
CREATE TABLE users (
    id        INT IDENTITY(1,1) PRIMARY KEY,
    email     NVARCHAR(150) NOT NULL UNIQUE,
    password  NVARCHAR(255) NOT NULL,
    full_name NVARCHAR(150) NOT NULL,
    phone     NVARCHAR(20),
    role      VARCHAR(30)  NOT NULL DEFAULT 'staff'
                           CONSTRAINT CK_users_role
                           CHECK (role IN ('manager','cashier','barista','staff')),
    active    BIT NOT NULL DEFAULT 1
);
GO

-- ─── customers (loyalty card owners) ────────────────────────────────────────
CREATE TABLE customers (
    id         INT IDENTITY(1,1) PRIMARY KEY,
    full_name  NVARCHAR(150) NOT NULL,
    phone      NVARCHAR(20) UNIQUE,
    email      NVARCHAR(150),
    points     INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT GETDATE()
);
GO

-- ─── cards ───────────────────────────────────────────────────────────────────
CREATE TABLE cards (
    id          INT IDENTITY(1,1) PRIMARY KEY,
    code        NVARCHAR(50) NOT NULL UNIQUE,
    customer_id INT,                         -- FK → customers (nullable for anonymous)
    status      BIT NOT NULL DEFAULT 1,
    CONSTRAINT FK_cards_customers FOREIGN KEY (customer_id) REFERENCES customers(id)
);
GO

-- ─── cafe_tables ─────────────────────────────────────────────────────────────
CREATE TABLE cafe_tables (
    id       INT IDENTITY(1,1) PRIMARY KEY,
    name     NVARCHAR(50) NOT NULL,           -- e.g. "Bàn 1", "Bàn VIP 3"
    capacity INT NOT NULL DEFAULT 4,
    active   BIT NOT NULL DEFAULT 1
);
GO

-- ─── bills ───────────────────────────────────────────────────────────────────
CREATE TABLE bills (
    id             INT IDENTITY(1,1) PRIMARY KEY,
    user_id        INT NOT NULL,
    card_id        INT,
    table_id       INT,                        -- NULL = takeaway / mang về
    code           NVARCHAR(50) NOT NULL,
    created_at     DATETIME NOT NULL DEFAULT GETDATE(),
    total          DECIMAL(15,2) NOT NULL DEFAULT 0,
    status         NVARCHAR(20)  NOT NULL DEFAULT 'waiting',  -- waiting|finish|cancel
    payment_method NVARCHAR(30)  NOT NULL DEFAULT 'cash',     -- cash|card|momo|zalopay
    CONSTRAINT FK_bills_users       FOREIGN KEY (user_id)  REFERENCES users(id),
    CONSTRAINT FK_bills_cards       FOREIGN KEY (card_id)  REFERENCES cards(id),
    CONSTRAINT FK_bills_cafe_tables FOREIGN KEY (table_id) REFERENCES cafe_tables(id)
);
GO

-- ─── bill_details ────────────────────────────────────────────────────────────
CREATE TABLE bill_details (
    id       INT IDENTITY(1,1) PRIMARY KEY,
    bill_id  INT NOT NULL,
    drink_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price    DECIMAL(15,2) NOT NULL DEFAULT 0,
    size     VARCHAR(2) NOT NULL DEFAULT 'M'   -- S / M / L
                        CONSTRAINT CK_bill_details_size CHECK (size IN ('S','M','L')),
    note     NVARCHAR(200),                    -- e.g. "ít đường, nhiều đá"
    CONSTRAINT FK_bill_details_bills  FOREIGN KEY (bill_id)  REFERENCES bills(id),
    CONSTRAINT FK_bill_details_drinks FOREIGN KEY (drink_id) REFERENCES drinks(id)
);
GO

-- ─── ingredients (stock / inventory) ─────────────────────────────────────────
CREATE TABLE ingredients (
    id       INT IDENTITY(1,1) PRIMARY KEY,
    name     NVARCHAR(150) NOT NULL,
    unit     NVARCHAR(30) NOT NULL,            -- e.g. "kg", "lít", "gói"
    stock    DECIMAL(10,3) NOT NULL DEFAULT 0,
    min_stock DECIMAL(10,3) NOT NULL DEFAULT 0 -- alert threshold
);
GO

-- ─── drink_ingredients (recipe) ──────────────────────────────────────────────
CREATE TABLE drink_ingredients (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    drink_id      INT NOT NULL,
    ingredient_id INT NOT NULL,
    qty_per_cup   DECIMAL(10,4) NOT NULL,      -- how much per one cup (size M)
    CONSTRAINT FK_di_drinks      FOREIGN KEY (drink_id)      REFERENCES drinks(id),
    CONSTRAINT FK_di_ingredients FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
    CONSTRAINT UQ_di UNIQUE (drink_id, ingredient_id)
);
GO

-- ─── stock_movements (audit trail) ───────────────────────────────────────────
CREATE TABLE stock_movements (
    id            INT IDENTITY(1,1) PRIMARY KEY,
    ingredient_id INT NOT NULL,
    delta         DECIMAL(10,3) NOT NULL,      -- positive = restock, negative = consumed
    reason        NVARCHAR(200),
    created_at    DATETIME NOT NULL DEFAULT GETDATE(),
    user_id       INT,
    CONSTRAINT FK_sm_ingredients FOREIGN KEY (ingredient_id) REFERENCES ingredients(id),
    CONSTRAINT FK_sm_users       FOREIGN KEY (user_id)       REFERENCES users(id)
);
GO

-- ─── Seed data ────────────────────────────────────────────────────────────────

INSERT INTO categories (name, active) VALUES
    (N'Cà phê', 1), (N'Trà', 1), (N'Sinh tố', 1), (N'Nước ép', 0);
GO

INSERT INTO users (email, password, full_name, phone, role, active) VALUES
    (N'admin@polycoffee.com',      N'123456', N'Nguyễn Văn Quản Lý', N'0901234567', 'manager', 1),
    (N'nhanvien01@polycoffee.com', N'123456', N'Trần Thị Thu Ngân',  N'0907654321', 'cashier', 1),
    (N'nhanvien02@polycoffee.com', N'123456', N'Lê Văn Pha Chế',     N'0912345678', 'barista', 1);
GO

INSERT INTO drinks (category_id, name, price, image, description, active) VALUES
    (1, N'Cà phê đen',  25000, N'caphe_den.jpg',  N'Cà phê đen truyền thống', 1),
    (1, N'Cà phê sữa',  30000, N'caphe_sua.jpg',  N'Cà phê sữa đá',           1),
    (2, N'Trà đào',     35000, N'tra_dao.jpg',    N'Trà đào cam sả',           1),
    (2, N'Trà sữa',     30000, N'tra_sua.jpg',    N'Trà sữa trân châu',        1),
    (3, N'Sinh tố bơ',  40000, N'sinhto_bo.jpg',  N'Sinh tố bơ tươi',          1),
    (3, N'Sinh tố dâu', 38000, N'sinhto_dau.jpg', N'Sinh tố dâu tây',          1);
GO

INSERT INTO cafe_tables (name, capacity, active) VALUES
    (N'Bàn 1', 2, 1), (N'Bàn 2', 2, 1), (N'Bàn 3', 4, 1),
    (N'Bàn 4', 4, 1), (N'Bàn VIP 1', 6, 1), (N'Bàn VIP 2', 6, 1);
GO

INSERT INTO customers (full_name, phone, email, points) VALUES
    (N'Nguyễn Thị Khách Hàng', N'0912000001', N'khach1@email.com', 150);
GO

INSERT INTO cards (code, customer_id, status) VALUES
    (N'CARD001', 1, 1),
    (N'CARD002', NULL, 1);
GO

INSERT INTO ingredients (name, unit, stock, min_stock) VALUES
    (N'Cà phê hạt', N'kg',  5.000, 1.000),
    (N'Sữa đặc',    N'lon', 20.000, 5.000),
    (N'Trà đào',    N'gói', 10.000, 2.000),
    (N'Bơ',         N'kg',  3.000, 0.500);
GO
