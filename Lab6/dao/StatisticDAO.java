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

    /**
     * Thống kê 5 sản phẩm bán chạy nhất
     */
    public List<BestSellingDrink> getTop5BestSellingDrinks(String fromDate, String toDate) {
        List<BestSellingDrink> list = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_Top5BestSellingDrinks(?, ?)}")) {

            if (fromDate != null && !fromDate.isEmpty()) {
                cs.setString(1, fromDate);
            } else {
                cs.setNull(1, java.sql.Types.DATE);
            }
            if (toDate != null && !toDate.isEmpty()) {
                cs.setString(2, toDate);
            } else {
                cs.setNull(2, java.sql.Types.DATE);
            }

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Thống kê doanh thu theo ngày
     */
    public List<Revenue> getRevenueByDay(String fromDate, String toDate) {
        List<Revenue> list = new ArrayList<>();
        try (Connection conn = JdbcUtil.getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_RevenueByDay(?, ?)}")) {

            if (fromDate != null && !fromDate.isEmpty()) {
                cs.setString(1, fromDate);
            } else {
                cs.setNull(1, java.sql.Types.DATE);
            }
            if (toDate != null && !toDate.isEmpty()) {
                cs.setString(2, toDate);
            } else {
                cs.setNull(2, java.sql.Types.DATE);
            }

            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Revenue item = new Revenue();
                item.setBillDate(rs.getDate("billDate"));
                item.setTotalBills(rs.getInt("totalBills"));
                item.setTotalRevenue(rs.getDouble("totalRevenue"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
