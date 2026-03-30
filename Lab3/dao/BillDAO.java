package dao;

import entity.Bill;
import utils.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO implements CrudDAO<Bill, Integer> {

    @Override
    public int update(Bill entity) {
        String sql = "UPDATE bills SET user_id = ?, card_id = ?, code = ?, created_at = ?, total = ?, status = ? WHERE id = ?";
        return JdbcUtil.executeUpdate(sql,
                entity.getUserId(), entity.getCardId() == 0 ? null : entity.getCardId(),
                entity.getCode(), entity.getCreatedAt(), entity.getTotal(), entity.getStatus(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        return JdbcUtil.executeUpdate("DELETE FROM bills WHERE id = ?", id);
    }

    @Override
    public Bill findById(Integer id) {
        List<Bill> list = findBySql("SELECT * FROM bills WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int create(Bill entity) {
        String sql = "INSERT INTO bills(user_id, card_id, code, created_at, total, status) VALUES (?, ?, ?, ?, ?, ?)";
        return JdbcUtil.executeUpdate(sql,
                entity.getUserId(), entity.getCardId() == 0 ? null : entity.getCardId(),
                entity.getCode(), entity.getCreatedAt(), entity.getTotal(), entity.getStatus());
    }

    @Override
    public List<Bill> findAll() {
        return findBySql("SELECT * FROM bills ORDER BY id DESC");
    }

    @Override
    public List<Bill> findBySql(String sql, Object... values) {
        List<Bill> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, values);
            while (rs != null && rs.next()) {
                Bill bill = new Bill();
                bill.setId(rs.getInt("id"));
                bill.setUserId(rs.getInt("user_id"));
                bill.setCardId(rs.getInt("card_id"));
                bill.setCode(rs.getString("code"));
                bill.setCreatedAt(rs.getTimestamp("created_at"));
                bill.setTotal(rs.getDouble("total"));
                bill.setStatus(rs.getString("status"));
                list.add(bill);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== Lab 6: Business Logic ====================

    public int createAndGetId(int userId) {
        String code = "HD" + System.currentTimeMillis();
        String sql = "INSERT INTO bills(user_id, code, created_at, total, status) " +
                     "VALUES (?, ?, GETDATE(), 0, 'waiting'); SELECT SCOPE_IDENTITY();";
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Cập nhật trạng thái đơn hàng với validation:
     * waiting -> finish (OK)
     * waiting -> cancel (OK)
     * finish  -> cancel (chỉ Admin)
     * Các chuyển đổi khác -> không cho phép
     */
    public int updateStatus(int billId, String newStatus) {
        Bill bill = findById(billId);
        if (bill == null) return 0;

        String current = bill.getStatus();
        // Không cho chuyển trạng thái nếu đã hủy
        if ("cancel".equals(current)) return -3;
        // Không cho chuyển từ finish về waiting
        if ("finish".equals(current) && "waiting".equals(newStatus)) return -3;

        return JdbcUtil.executeUpdate("UPDATE bills SET status = ? WHERE id = ?", newStatus, billId);
    }

    public int updateTotal(int billId) {
        String sql = "UPDATE bills SET total = " +
                     "(SELECT ISNULL(SUM(quantity * price), 0) FROM bill_details WHERE bill_id = ?) " +
                     "WHERE id = ?";
        return JdbcUtil.executeUpdate(sql, billId, billId);
    }

    public List<Bill> findByUserId(int userId) {
        return findBySql("SELECT * FROM bills WHERE user_id = ? ORDER BY id DESC", userId);
    }

    public Bill findWaitingBill(int userId) {
        List<Bill> list = findBySql(
                "SELECT * FROM bills WHERE user_id = ? AND status = 'waiting' ORDER BY id DESC", userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Bill> findAllPaged(int page, int pageSize) {
        return findBySql("SELECT * FROM bills ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                (page - 1) * pageSize, pageSize);
    }

    public int countAll() {
        try {
            ResultSet rs = JdbcUtil.executeQuery("SELECT COUNT(*) FROM bills");
            if (rs != null && rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
}
