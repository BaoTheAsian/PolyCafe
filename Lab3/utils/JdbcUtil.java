package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcUtil {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=PolyCoffee;encrypt=false";
    private static final String USER = "sa";
    private static final String PASSWORD = "123";
    /**
     * Tạo kết nối đến CSDL SQL Server
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Thực thi câu lệnh SELECT, trả về ResultSet
     */
    public static ResultSet executeQuery(String sql, Object... values) {
        try {
            Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Thực thi câu lệnh INSERT, UPDATE, DELETE, trả về số dòng bị ảnh hưởng
     * Trả về -1 nếu trùng dữ liệu (duplicate key)
     * Trả về -2 nếu vi phạm khóa ngoại (FK constraint)
     */
    public static int executeUpdate(String sql, Object... values) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < values.length; i++) {
                ps.setObject(i + 1, values[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            // SQL Server error codes
            // 2627 / 2601 = Duplicate key (UNIQUE/PRIMARY KEY constraint)
            // 547 = FK constraint violation (DELETE/UPDATE blocked by child records)
            int errorCode = e.getErrorCode();
            if (errorCode == 2627 || errorCode == 2601) {
                System.err.println("[DB] Dữ liệu bị trùng: " + e.getMessage());
                return -1;
            }
            if (errorCode == 547) {
                System.err.println("[DB] Không thể xóa vì có dữ liệu liên quan: " + e.getMessage());
                return -2;
            }
            e.printStackTrace();
        }
        return 0;
    }
}
