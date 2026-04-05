package dao;

import entity.Bill;
import utils.JdbcUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillDAO implements CrudDAO<Bill, Integer> {

    @Override
    public int create(Bill entity) {
        return JdbcUtil.executeUpdate(
                "INSERT INTO bills(user_id, card_id, table_id, code, created_at, total, status, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                entity.getUserId(),
                entity.getCardId() == 0 ? null : entity.getCardId(),
                entity.getTableId() == 0 ? null : entity.getTableId(),
                entity.getCode(), entity.getCreatedAt(), entity.getTotal(),
                entity.getStatus(), entity.getPaymentMethod());
    }

    @Override
    public int update(Bill entity) {
        return JdbcUtil.executeUpdate(
                "UPDATE bills SET user_id=?, card_id=?, table_id=?, code=?, created_at=?, total=?, status=?, payment_method=? WHERE id=?",
                entity.getUserId(),
                entity.getCardId() == 0 ? null : entity.getCardId(),
                entity.getTableId() == 0 ? null : entity.getTableId(),
                entity.getCode(), entity.getCreatedAt(), entity.getTotal(),
                entity.getStatus(), entity.getPaymentMethod(), entity.getId());
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
    public List<Bill> findAll() {
        return findBySql("SELECT * FROM bills ORDER BY id DESC");
    }

    @Override
    public List<Bill> findBySql(String sql, Object... values) {
        List<Bill> list = new ArrayList<>();
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql, values);
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                Bill b = new Bill();
                b.setId(rs.getInt("id"));
                b.setUserId(rs.getInt("user_id"));
                b.setCardId(rs.getInt("card_id"));
                b.setTableId(rs.getInt("table_id"));
                b.setCode(rs.getString("code"));
                b.setCreatedAt(rs.getTimestamp("created_at"));
                b.setTotal(rs.getDouble("total"));
                b.setStatus(rs.getString("status"));
                b.setPaymentMethod(rs.getString("payment_method"));
                list.add(b);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }

    public int createAndGetId(int userId) {
        String code = "HD" + System.currentTimeMillis();
        String sql = "INSERT INTO bills(user_id, code, created_at, total, status, payment_method) " +
                     "VALUES (?, ?, GETDATE(), 0, 'waiting', 'cash'); SELECT SCOPE_IDENTITY();";
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
     * Update bill status with transition validation:
     * waiting → finish (OK) | waiting → cancel (OK)
     * finish  → cancel (admin only — checked at servlet level)
     * cancel  → anything (blocked)
     */
    public int updateStatus(int billId, String newStatus) {
        Bill bill = findById(billId);
        if (bill == null) return 0;
        String current = bill.getStatus();
        if ("cancel".equals(current)) return -3;
        if ("finish".equals(current) && "waiting".equals(newStatus)) return -3;
        return JdbcUtil.executeUpdate("UPDATE bills SET status = ? WHERE id = ?", newStatus, billId);
    }

    public int updateStatusAndPayment(int billId, String newStatus, String paymentMethod) {
        Bill bill = findById(billId);
        if (bill == null) return 0;
        if ("cancel".equals(bill.getStatus())) return -3;
        return JdbcUtil.executeUpdate(
                "UPDATE bills SET status = ?, payment_method = ? WHERE id = ?",
                newStatus, paymentMethod, billId);
    }

    public int updateTotal(int billId) {
        return JdbcUtil.executeUpdate(
                "UPDATE bills SET total = " +
                "(SELECT ISNULL(SUM(quantity * price), 0) FROM bill_details WHERE bill_id = ?) " +
                "WHERE id = ?", billId, billId);
    }

    public Bill findWaitingBill(int userId) {
        List<Bill> list = findBySql(
                "SELECT * FROM bills WHERE user_id = ? AND status = 'waiting' ORDER BY id DESC", userId);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Bill> findAllPaged(int page, int pageSize) {
        return findBySql(
                "SELECT * FROM bills ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY",
                (page - 1) * pageSize, pageSize);
    }

    public List<Bill> searchPaged(String fromDate, String toDate, String status, String keyword, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM bills WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND CAST(created_at AS DATE) >= ?"); params.add(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND CAST(created_at AS DATE) <= ?"); params.add(toDate);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?"); params.add(status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND code LIKE ?"); params.add("%" + keyword.trim() + "%");
        }
        sql.append(" ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);
        return findBySql(sql.toString(), params.toArray());
    }

    public int countSearch(String fromDate, String toDate, String status, String keyword) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM bills WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND CAST(created_at AS DATE) >= ?"); params.add(fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND CAST(created_at AS DATE) <= ?"); params.add(toDate);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?"); params.add(status);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND code LIKE ?"); params.add("%" + keyword.trim() + "%");
        }
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql.toString(), params.toArray());
        try {
            if (h != null && h.rs().next()) return h.rs().getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return 0;
    }

    public int countAll() {
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery("SELECT COUNT(*) FROM bills");
        try {
            if (h != null && h.rs().next()) return h.rs().getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return 0;
    }
}
