package dao;

import entity.Drink;
import utils.JdbcUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DrinkDAO implements CrudDAO<Drink, Integer> {

    @Override
    public int create(Drink entity) {
        if (entity.getPrice() < 0) return -3;
        return JdbcUtil.executeUpdate(
                "INSERT INTO drinks(category_id, name, price, image, description, active) VALUES (?, ?, ?, ?, ?, ?)",
                entity.getCategoryId(), entity.getName(), entity.getPrice(),
                entity.getImage(), entity.getDescription(), entity.isActive());
    }

    @Override
    public int update(Drink entity) {
        if (entity.getPrice() < 0) return -3;
        return JdbcUtil.executeUpdate(
                "UPDATE drinks SET category_id = ?, name = ?, price = ?, image = ?, description = ?, active = ? WHERE id = ?",
                entity.getCategoryId(), entity.getName(), entity.getPrice(),
                entity.getImage(), entity.getDescription(), entity.isActive(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        return JdbcUtil.executeUpdate("DELETE FROM drinks WHERE id = ?", id);
    }

    @Override
    public List<Drink> findAll() {
        return findBySql("SELECT * FROM drinks");
    }

    @Override
    public Drink findById(Integer id) {
        List<Drink> list = findBySql("SELECT * FROM drinks WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Drink> findBySql(String sql, Object... values) {
        List<Drink> list = new ArrayList<>();
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql, values);
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                Drink d = new Drink();
                d.setId(rs.getInt("id"));
                d.setCategoryId(rs.getInt("category_id"));
                d.setName(rs.getString("name"));
                d.setPrice(rs.getDouble("price"));
                d.setImage(rs.getString("image"));
                d.setDescription(rs.getString("description"));
                d.setActive(rs.getBoolean("active"));
                list.add(d);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }

    public List<Drink> search(String keyword, int categoryId, int active, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM drinks WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?"); params.add("%" + keyword.trim() + "%");
        }
        if (categoryId > 0) { sql.append(" AND category_id = ?"); params.add(categoryId); }
        if (active >= 0)    { sql.append(" AND active = ?");      params.add(active); }
        sql.append(" ORDER BY id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);
        return findBySql(sql.toString(), params.toArray());
    }

    public int count(String keyword, int categoryId, int active) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM drinks WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?"); params.add("%" + keyword.trim() + "%");
        }
        if (categoryId > 0) { sql.append(" AND category_id = ?"); params.add(categoryId); }
        if (active >= 0)    { sql.append(" AND active = ?");      params.add(active); }
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql.toString(), params.toArray());
        try {
            if (h != null && h.rs().next()) return h.rs().getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return 0;
    }
}
