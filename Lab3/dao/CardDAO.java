package dao;

import entity.Card;
import utils.JdbcUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardDAO implements CrudDAO<Card, Integer> {

    @Override
    public int create(Card entity) {
        return JdbcUtil.executeUpdate(
                "INSERT INTO cards(code, status) VALUES (?, ?)",
                entity.getCode(), entity.isStatus());
    }

    @Override
    public int update(Card entity) {
        return JdbcUtil.executeUpdate(
                "UPDATE cards SET code = ?, status = ? WHERE id = ?",
                entity.getCode(), entity.isStatus(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        return JdbcUtil.executeUpdate("DELETE FROM cards WHERE id = ?", id);
    }

    @Override
    public List<Card> findAll() { return findBySql("SELECT * FROM cards"); }

    @Override
    public Card findById(Integer id) {
        List<Card> list = findBySql("SELECT * FROM cards WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Card findByCode(String code) {
        List<Card> list = findBySql("SELECT * FROM cards WHERE code = ?", code);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Card> findBySql(String sql, Object... values) {
        List<Card> list = new ArrayList<>();
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql, values);
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                Card c = new Card();
                c.setId(rs.getInt("id"));
                c.setCode(rs.getString("code"));
                c.setStatus(rs.getBoolean("status"));
                list.add(c);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }
}
