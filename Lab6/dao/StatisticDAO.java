package dao;

import entity.BestSellingDrink;
import entity.Revenue;
import utils.JdbcUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {

    public List<BestSellingDrink> getTop5BestSellingDrinks(String fromDate, String toDate) {
        List<BestSellingDrink> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT TOP 5 d.id, d.name, d.price, d.image, " +
            "SUM(bd.quantity) AS totalQuantity, " +
            "SUM(bd.quantity * bd.price) AS totalRevenue " +
            "FROM bill_details bd " +
            "INNER JOIN drinks d ON bd.drink_id = d.id " +
            "INNER JOIN bills b ON bd.bill_id = b.id " +
            "WHERE b.status = 'finish'"
        );
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND CAST(b.created_at AS DATE) >= ?");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND CAST(b.created_at AS DATE) <= ?");
            params.add(toDate);
        }
        sql.append(" GROUP BY d.id, d.name, d.price, d.image ORDER BY totalQuantity DESC");

        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql.toString(), params.toArray());
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                BestSellingDrink item = new BestSellingDrink();
                item.setId(rs.getInt("id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setImage(rs.getString("image"));
                item.setTotalQuantity(rs.getInt("totalQuantity"));
                item.setTotalRevenue(rs.getDouble("totalRevenue"));
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }

    public List<Revenue> getRevenueByDay(String fromDate, String toDate) {
        List<Revenue> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT CAST(b.created_at AS DATE) AS billDate, " +
            "COUNT(b.id) AS totalBills, " +
            "SUM(b.total) AS totalRevenue " +
            "FROM bills b WHERE b.status = 'finish'"
        );
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND CAST(b.created_at AS DATE) >= ?");
            params.add(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND CAST(b.created_at AS DATE) <= ?");
            params.add(toDate);
        }
        sql.append(" GROUP BY CAST(b.created_at AS DATE) ORDER BY billDate ASC");

        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql.toString(), params.toArray());
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                Revenue item = new Revenue();
                item.setBillDate(rs.getDate("billDate"));
                item.setTotalBills(rs.getInt("totalBills"));
                item.setTotalRevenue(rs.getDouble("totalRevenue"));
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }

    public List<Object[]> getRevenueByPaymentMethod(String fromDate, String toDate) {
        List<Object[]> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT payment_method, SUM(total) AS total_revenue, COUNT(*) AS bill_count " +
                "FROM bills WHERE status = 'finish'");
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND CAST(created_at AS DATE) >= ?"); params.add(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND CAST(created_at AS DATE) <= ?"); params.add(toDate);
        }
        sql.append(" GROUP BY payment_method ORDER BY total_revenue DESC");
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql.toString(), params.toArray());
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                list.add(new Object[]{
                    rs.getString("payment_method"),
                    rs.getDouble("total_revenue"),
                    rs.getInt("bill_count")
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }
}
