-- =============================================
-- Lab 6: Stored Procedures thống kê
-- =============================================
USE PolyCoffee;
GO

-- Thống kê 5 sản phẩm bán chạy nhất
CREATE OR ALTER PROCEDURE sp_Top5BestSellingDrinks
    @fromDate DATE = NULL,
    @toDate DATE = NULL
AS
BEGIN
    SELECT TOP 5
        d.id, d.name, d.price, d.image,
        SUM(bd.quantity) AS totalQuantity,
        SUM(bd.quantity * bd.price) AS totalRevenue
    FROM bill_details bd
    INNER JOIN drinks d ON bd.drink_id = d.id
    INNER JOIN bills b ON bd.bill_id = b.id
    WHERE b.status = 'finish'
        AND (@fromDate IS NULL OR CAST(b.created_at AS DATE) >= @fromDate)
        AND (@toDate IS NULL OR CAST(b.created_at AS DATE) <= @toDate)
    GROUP BY d.id, d.name, d.price, d.image
    ORDER BY totalQuantity DESC
END
GO

-- Thống kê doanh thu theo ngày
CREATE OR ALTER PROCEDURE sp_RevenueByDay
    @fromDate DATE = NULL,
    @toDate DATE = NULL
AS
BEGIN
    SELECT
        CAST(b.created_at AS DATE) AS billDate,
        COUNT(b.id) AS totalBills,
        SUM(b.total) AS totalRevenue
    FROM bills b
    WHERE b.status = 'finish'
        AND (@fromDate IS NULL OR CAST(b.created_at AS DATE) >= @fromDate)
        AND (@toDate IS NULL OR CAST(b.created_at AS DATE) <= @toDate)
    GROUP BY CAST(b.created_at AS DATE)
    ORDER BY billDate ASC
END
GO
