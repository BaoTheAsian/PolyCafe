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
        // Kiểm tra trùng tên
        if (existsByName(entity.getName(), 0)) return -1;
        String sql = "INSERT INTO categories(name, active) VALUES (?, ?)";
        return JdbcUtil.executeUpdate(sql, entity.getName(), entity.isActive());
    }

    @Override
    public int update(Category entity) {
        // Kiểm tra trùng tên (trừ chính nó)
        if (existsByName(entity.getName(), entity.getId())) return -1;
        String sql = "UPDATE categories SET name = ?, active = ? WHERE id = ?";
        return JdbcUtil.executeUpdate(sql, entity.getName(), entity.isActive(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        // JdbcUtil trả về -2 nếu có drinks liên quan (FK constraint)
        String sql = "DELETE FROM categories WHERE id = ?";
        return JdbcUtil.executeUpdate(sql, id);
    }

    /** Kiểm tra tên loại đồ uống đã tồn tại chưa (excludeId = 0 khi tạo mới) */
    public boolean existsByName(String name, int excludeId) {
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ? AND id != ?";
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, name, excludeId);
            if (rs != null && rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    @Override
    public List<Category> findAll() {
        String sql = "SELECT * FROM categories";
        return findBySql(sql);
    }

    @Override
    public Category findById(Integer id) {
        String sql = "SELECT * FROM categories WHERE id = ?";
        List<Category> list = findBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Category> findBySql(String sql, Object... values) {
        List<Category> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, values);
            while (rs != null && rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setName(rs.getString("name"));
                category.setActive(rs.getBoolean("active"));
                list.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
