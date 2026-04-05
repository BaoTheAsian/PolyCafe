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
        return JdbcUtil.executeUpdate(
                "INSERT INTO bill_details(bill_id, drink_id, quantity, price, size, note) VALUES (?, ?, ?, ?, ?, ?)",
                entity.getBillId(), entity.getDrinkId(), entity.getQuantity(),
                entity.getPrice(), entity.getSize(), entity.getNote());
    }

    @Override
    public int update(BillDetail entity) {
        return JdbcUtil.executeUpdate(
                "UPDATE bill_details SET bill_id=?, drink_id=?, quantity=?, price=?, size=?, note=? WHERE id=?",
                entity.getBillId(), entity.getDrinkId(), entity.getQuantity(),
                entity.getPrice(), entity.getSize(), entity.getNote(), entity.getId());
    }

    @Override
    public int delete(Integer id) {
        return JdbcUtil.executeUpdate("DELETE FROM bill_details WHERE id = ?", id);
    }

    @Override
    public List<BillDetail> findAll() { return findBySql("SELECT * FROM bill_details"); }

    @Override
    public BillDetail findById(Integer id) {
        List<BillDetail> list = findBySql("SELECT * FROM bill_details WHERE id = ?", id);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public List<BillDetail> findBySql(String sql, Object... values) {
        List<BillDetail> list = new ArrayList<>();
        JdbcUtil.ResultSetHolder h = JdbcUtil.executeQuery(sql, values);
        try {
            ResultSet rs = h != null ? h.rs() : null;
            while (rs != null && rs.next()) {
                BillDetail bd = new BillDetail();
                bd.setId(rs.getInt("id"));
                bd.setBillId(rs.getInt("bill_id"));
                bd.setDrinkId(rs.getInt("drink_id"));
                bd.setDrinkName(rs.getString("drink_name"));
                bd.setQuantity(rs.getInt("quantity"));
                bd.setPrice(rs.getDouble("price"));
                bd.setSize(rs.getString("size"));
                bd.setNote(rs.getString("note"));
                list.add(bd);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h); }
        return list;
    }

    /** Fetch bill details joined with drink name — used in POS and bill detail views. */
    public List<BillDetail> findByBillId(int billId) {
        return findBySql(
                "SELECT bd.*, d.name AS drink_name " +
                "FROM bill_details bd " +
                "INNER JOIN drinks d ON bd.drink_id = d.id " +
                "WHERE bd.bill_id = ?", billId);
    }

    public void addDrinkToBill(int billId, int drinkId, double price, String size, String note) {
        BillDetail existing = findByBillDrinkSize(billId, drinkId, size);
        if (existing != null) {
            updateQuantity(existing.getId(), existing.getQuantity() + 1);
        } else {
            BillDetail bd = new BillDetail();
            bd.setBillId(billId);
            bd.setDrinkId(drinkId);
            bd.setQuantity(1);
            bd.setPrice(price);
            bd.setSize(size != null ? size : "M");
            bd.setNote(note);
            create(bd);
        }
    }

    public void updateQuantity(int detailId, int newQuantity) {
        if (newQuantity <= 0) delete(detailId);
        else JdbcUtil.executeUpdate("UPDATE bill_details SET quantity = ? WHERE id = ?", newQuantity, detailId);
    }

    private BillDetail findByBillDrinkSize(int billId, int drinkId, String size) {
        List<BillDetail> list = findBySql(
                "SELECT bd.*, d.name AS drink_name " +
                "FROM bill_details bd INNER JOIN drinks d ON bd.drink_id = d.id " +
                "WHERE bd.bill_id = ? AND bd.drink_id = ? AND ISNULL(bd.size,'M') = ISNULL(?,'M')",
                billId, drinkId, size != null ? size : "M");
        return list.isEmpty() ? null : list.get(0);
    }
}
