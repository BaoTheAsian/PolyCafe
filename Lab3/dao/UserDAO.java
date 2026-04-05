package dao;

import entity.User;
import utils.JdbcUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements CrudDAO<User, Integer> {

    @Override
    public int create(User entity) {
        return JdbcUtil.executeUpdate(
                "INSERT INTO users(email, password, full_name, phone, role, active) VALUES (?, ?, ?, ?, ?, ?)",
                entity.getEmail(), entity.getPassword(), entity.getFullName(),
                entity.getPhone(), entity.getRole(), entity.isActive());
    }

    @Override
    public int update(User entity) {
        return JdbcUtil.executeUpdate(
                "UPDATE users SET email = ?, password = ?, full_name = ?, phone = ?, role = ?, active = ? WHERE id = ?",
                entity.getEmail(), entity.getPassword(), entity.getFullName(),
                entity.getPhone(), entity.getRole(), entity.isActive(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        return JdbcUtil.executeUpdate("DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public List<User> findAll() {
        return findBySql("SELECT * FROM users");
    }

    @Override
    public User findById(Integer id) {
        List<User> list = findBySql("SELECT * FROM users WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<User> findBySql(String sql, Object... values) {
        List<User> list = new ArrayList<>();
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql, values);
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                User u = new User();
                u.setId(rs.getInt("id"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                u.setPhone(rs.getString("phone"));
                u.setRole(rs.getString("role"));
                u.setActive(rs.getBoolean("active"));
                list.add(u);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }

    public User findByEmail(String email) {
        List<User> list = findBySql("SELECT * FROM users WHERE email = ?", email);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<User> searchStaff(String keyword, String email, String role, int active, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE role != 'manager'");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND full_name LIKE ?"); params.add("%" + keyword.trim() + "%");
        }
        if (email != null && !email.trim().isEmpty()) {
            sql.append(" AND email LIKE ?"); params.add("%" + email.trim() + "%");
        }
        if (role != null && !role.trim().isEmpty()) {
            sql.append(" AND role = ?"); params.add(role.trim());
        }
        if (active >= 0) { sql.append(" AND active = ?"); params.add(active); }
        sql.append(" ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);
        return findBySql(sql.toString(), params.toArray());
    }

    public int countStaff(String keyword, String email, String role, int active) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE role != 'manager'");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND full_name LIKE ?"); params.add("%" + keyword.trim() + "%");
        }
        if (email != null && !email.trim().isEmpty()) {
            sql.append(" AND email LIKE ?"); params.add("%" + email.trim() + "%");
        }
        if (role != null && !role.trim().isEmpty()) {
            sql.append(" AND role = ?"); params.add(role.trim());
        }
        if (active >= 0) { sql.append(" AND active = ?"); params.add(active); }
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql.toString(), params.toArray());
        try {
            if (h != null && h.rs().next()) return h.rs().getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return 0;
    }

    public int updatePassword(int userId, String newPassword) {
        return JdbcUtil.executeUpdate("UPDATE users SET password = ? WHERE id = ?", newPassword, userId);
    }

    public int updateActive(int userId, boolean active) {
        return JdbcUtil.executeUpdate("UPDATE users SET active = ? WHERE id = ?", active, userId);
    }
}
