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
        String sql = "INSERT INTO cards(code, status) VALUES (?, ?)";
        return JdbcUtil.executeUpdate(sql, entity.getCode(), entity.isStatus());
    }

    @Override
    public int update(Card entity) {
        String sql = "UPDATE cards SET code = ?, status = ? WHERE id = ?";
        return JdbcUtil.executeUpdate(sql, entity.getCode(), entity.isStatus(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM cards WHERE id = ?";
        return JdbcUtil.executeUpdate(sql, id);
    }

    @Override
    public List<Card> findAll() {
        String sql = "SELECT * FROM cards";
        return findBySql(sql);
    }

    @Override
    public Card findById(Integer id) {
        String sql = "SELECT * FROM cards WHERE id = ?";
        List<Card> list = findBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<Card> findBySql(String sql, Object... values) {
        List<Card> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, values);
            while (rs != null && rs.next()) {
                Card card = new Card();
                card.setId(rs.getInt("id"));
                card.setCode(rs.getString("code"));
                card.setStatus(rs.getBoolean("status"));
                list.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
