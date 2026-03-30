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
        if (entity.getPrice() < 0) return -3; // Giá không hợp lệ
        String sql = "INSERT INTO drinks(category_id, name, price, image, description, active) VALUES (?, ?, ?, ?, ?, ?)";
        return JdbcUtil.executeUpdate(sql,
                entity.getCategoryId(), entity.getName(), entity.getPrice(),
                entity.getImage(), entity.getDescription(), entity.isActive());
    }

    @Override
    public int update(Drink entity) {
        if (entity.getPrice() < 0) return -3;
        String sql = "UPDATE drinks SET category_id = ?, name = ?, price = ?, image = ?, description = ?, active = ? WHERE id = ?";
        return JdbcUtil.executeUpdate(sql,
                entity.getCategoryId(), entity.getName(), entity.getPrice(),
                entity.getImage(), entity.getDescription(), entity.isActive(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        // JdbcUtil trả về -2 nếu đồ uống đã có trong hóa đơn (FK constraint)
        String sql = "DELETE FROM drinks WHERE id = ?";
        return JdbcUtil.executeUpdate(sql, id);
    }

    @Override
    public List<Drink> findAll() {
        String sql = "SELECT * FROM drinks";
        return findBySql(sql);
    }

    @Override
    public Drink findById(Integer id) {
        String sql = "SELECT * FROM drinks WHERE id = ?";
        List<Drink> list = findBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Drink> findBySql(String sql, Object... values) {
        List<Drink> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, values);
            while (rs != null && rs.next()) {
                Drink drink = new Drink();
                drink.setId(rs.getInt("id"));
                drink.setCategoryId(rs.getInt("category_id"));
                drink.setName(rs.getString("name"));
                drink.setPrice(rs.getDouble("price"));
                drink.setImage(rs.getString("image"));
                drink.setDescription(rs.getString("description"));
                drink.setActive(rs.getBoolean("active"));
                list.add(drink);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== Lab 5: Search + Pagination ====================

    public List<Drink> search(String keyword, int categoryId, int active, int page, int pageSize) {
        StringBuilder sql = new StringBuilder("SELECT * FROM drinks WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
        if (categoryId > 0) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
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

    public int count(String keyword, int categoryId, int active) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM drinks WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
        if (categoryId > 0) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
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
}
