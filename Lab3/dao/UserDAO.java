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
        String sql = "INSERT INTO users(email, password, full_name, phone, role, active) VALUES (?, ?, ?, ?, ?, ?)";
        return JdbcUtil.executeUpdate(sql,
                entity.getEmail(), entity.getPassword(), entity.getFullName(),
                entity.getPhone(), entity.isRole(), entity.isActive());
    }

    @Override
    public int update(User entity) {
        String sql = "UPDATE users SET email = ?, password = ?, full_name = ?, phone = ?, role = ?, active = ? WHERE id = ?";
        return JdbcUtil.executeUpdate(sql,
                entity.getEmail(), entity.getPassword(), entity.getFullName(),
                entity.getPhone(), entity.isRole(), entity.isActive(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM users WHERE id = ?";
        return JdbcUtil.executeUpdate(sql, id);
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return findBySql(sql);
    }

    @Override
    public User findById(Integer id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> list = findBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<User> findBySql(String sql, Object... values) {
        List<User> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, values);
            while (rs != null && rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setFullName(rs.getString("full_name"));
                user.setPhone(rs.getString("phone"));
                user.setRole(rs.getBoolean("role"));
                user.setActive(rs.getBoolean("active"));
                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== Lab 4: findByEmail ====================

    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        List<User> list = findBySql(sql, email);
        return list.isEmpty() ? null : list.get(0);
    }

    // ==================== Lab 5: Search + Pagination + Password ====================

    public List<User> searchStaff(String keyword, String email, int active, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE role = 0");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
        if (email != null && !email.trim().isEmpty()) {
            sql.append(" AND email LIKE ?");
            params.add("%" + email.trim() + "%");
        }
        if (active >= 0) {
            sql.append(" AND active = ?");
            params.add(active);
        }
        sql.append(" ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);
        return findBySql(sql.toString(), params.toArray());
    }

    public int countStaff(String keyword, String email, int active) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM users WHERE role = 0");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND full_name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
        if (email != null && !email.trim().isEmpty()) {
            sql.append(" AND email LIKE ?");
            params.add("%" + email.trim() + "%");
        }
        if (active >= 0) {
            sql.append(" AND active = ?");
            params.add(active);
        }
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql.toString(), params.toArray());
            if (rs != null && rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int updatePassword(int userId, String newPassword) {
        return JdbcUtil.executeUpdate("UPDATE users SET password = ? WHERE id = ?", newPassword, userId);
    }

    public int updateActive(int userId, boolean active) {
        return JdbcUtil.executeUpdate("UPDATE users SET active = ? WHERE id = ?", active, userId);
    }
}
