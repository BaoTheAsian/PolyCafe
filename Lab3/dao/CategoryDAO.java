package dao;

import entity.Category;
import utils.JdbcUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO implements CrudDAO<Category, Integer> {

    @Override
    public int create(Category entity) {
        if (existsByName(entity.getName(), 0)) return -1;
        return JdbcUtil.executeUpdate(
                "INSERT INTO categories(name, active) VALUES (?, ?)",
                entity.getName(), entity.isActive());
    }

    @Override
    public int update(Category entity) {
        if (existsByName(entity.getName(), entity.getId())) return -1;
        return JdbcUtil.executeUpdate(
                "UPDATE categories SET name = ?, active = ? WHERE id = ?",
                entity.getName(), entity.isActive(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        return JdbcUtil.executeUpdate("DELETE FROM categories WHERE id = ?", id);
    }

    public boolean existsByName(String name, int excludeId) {
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(
                "SELECT COUNT(*) FROM categories WHERE name = ? AND id != ?", name, excludeId);
        try {
            if (h != null && h.rs().next()) return h.rs().getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return false;
    }

    @Override
    public List<Category> findAll() {
        return findBySql("SELECT * FROM categories");
    }

    @Override
    public Category findById(Integer id) {
        List<Category> list = findBySql("SELECT * FROM categories WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Category> findBySql(String sql, Object... values) {
        List<Category> list = new ArrayList<>();
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql, values);
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                c.setActive(rs.getBoolean("active"));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }
}
