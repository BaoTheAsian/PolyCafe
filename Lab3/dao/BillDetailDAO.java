package dao;

import entity.BillDetail;
import utils.JdbcUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BillDetailDAO implements CrudDAO<BillDetail, Integer> {

    @Override
    public int create(BillDetail entity) {
        String sql = "INSERT INTO bill_details(bill_id, drink_id, quantity, price) VALUES (?, ?, ?, ?)";
        return JdbcUtil.executeUpdate(sql,
                entity.getBillId(), entity.getDrinkId(), entity.getQuantity(), entity.getPrice());
    }

    @Override
    public int update(BillDetail entity) {
        String sql = "UPDATE bill_details SET bill_id = ?, drink_id = ?, quantity = ?, price = ? WHERE id = ?";
        return JdbcUtil.executeUpdate(sql,
                entity.getBillId(), entity.getDrinkId(), entity.getQuantity(), entity.getPrice(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        String sql = "DELETE FROM bill_details WHERE id = ?";
        return JdbcUtil.executeUpdate(sql, id);
    }

    @Override
    public List<BillDetail> findAll() {
        String sql = "SELECT * FROM bill_details";
        return findBySql(sql);
    }

    @Override
    public BillDetail findById(Integer id) {
        String sql = "SELECT * FROM bill_details WHERE id = ?";
        List<BillDetail> list = findBySql(sql, id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<BillDetail> findBySql(String sql, Object... values) {
        List<BillDetail> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcUtil.executeQuery(sql, values);
            while (rs != null && rs.next()) {
                BillDetail detail = new BillDetail();
                detail.setId(rs.getInt("id"));
                detail.setBillId(rs.getInt("bill_id"));
                detail.setDrinkId(rs.getInt("drink_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setPrice(rs.getDouble("price"));
                list.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==================== Lab 6: Business Logic ====================

    public List<BillDetail> findByBillId(int billId) {
        return findBySql("SELECT * FROM bill_details WHERE bill_id = ?", billId);
    }

    public void addDrinkToBill(int billId, int drinkId, double price) {
        BillDetail existing = findByBillAndDrink(billId, drinkId);
        if (existing != null) {
            updateQuantity(existing.getId(), existing.getQuantity() + 1);
        } else {
            create(new BillDetail(0, billId, drinkId, 1, price));
        }
    }

    public void updateQuantity(int detailId, int newQuantity) {
        if (newQuantity <= 0) {
            delete(detailId);
        } else {
            JdbcUtil.executeUpdate("UPDATE bill_details SET quantity = ? WHERE id = ?", newQuantity, detailId);
        }
    }

    public BillDetail findByBillAndDrink(int billId, int drinkId) {
        List<BillDetail> list = findBySql(
                "SELECT * FROM bill_details WHERE bill_id = ? AND drink_id = ?", billId, drinkId);
        return list.isEmpty() ? null : list.get(0);
    }
}
