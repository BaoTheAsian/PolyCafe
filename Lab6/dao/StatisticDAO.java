package dao;

import entity.BestSellingDrink;
import entity.Revenue;
import utils.JdbcUtil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StatisticDAO {

    public List<BestSellingDrink> getTop5BestSellingDrinks(String fromDate, String toDate) {
        List<BestSellingDrink> list = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_Top5BestSellingDrinks(?, ?)}")) {
            cs.setString(1, (fromDate != null && !fromDate.isEmpty()) ? fromDate : null);
            cs.setString(2, (toDate   != null && !toDate.isEmpty())   ? toDate   : null);
            if (fromDate == null || fromDate.isEmpty()) cs.setNull(1, java.sql.Types.DATE);
            if (toDate   == null || toDate.isEmpty())   cs.setNull(2, java.sql.Types.DATE);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
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
        return list;
    }

    public List<Revenue> getRevenueByDay(String fromDate, String toDate) {
        List<Revenue> list = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_RevenueByDay(?, ?)}")) {
            if (fromDate != null && !fromDate.isEmpty()) cs.setString(1, fromDate);
            else cs.setNull(1, java.sql.Types.DATE);
            if (toDate != null && !toDate.isEmpty()) cs.setString(2, toDate);
            else cs.setNull(2, java.sql.Types.DATE);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Revenue item = new Revenue();
                item.setBillDate(rs.getDate("billDate"));
                item.setTotalBills(rs.getInt("totalBills"));
                item.setTotalRevenue(rs.getDouble("totalRevenue"));
                list.add(item);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    /** Revenue by payment method for a date range. */
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
