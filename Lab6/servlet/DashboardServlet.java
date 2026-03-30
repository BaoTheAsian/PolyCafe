package servlet;

import dao.BillDAO;
import dao.UserDAO;
import entity.Bill;
import entity.User;
import utils.AuthUtil;
import utils.JdbcUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@WebServlet(urlPatterns = "/manager/dashboard")
public class DashboardServlet extends HttpServlet {

    private BillDAO billDAO;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        billDAO = new BillDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── 1. Today's revenue & bill count ──────────────────────────────
        double todayRevenue = 0;
        int todayBillCount = 0;
        try {
            ResultSet rs = JdbcUtil.executeQuery(
                    "SELECT COUNT(*) AS bill_count, ISNULL(SUM(total), 0) AS revenue " +
                            "FROM bills " +
                            "WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE) " +
                            "  AND status <> 'cancel'"
            );
            if (rs != null && rs.next()) {
                todayBillCount = rs.getInt("bill_count");
                todayRevenue   = rs.getDouble("revenue");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // ── 2. Yesterday's revenue (for % change display) ────────────────
        double yesterdayRevenue = 0;
        int yesterdayBillCount = 0;
        try {
            ResultSet rs = JdbcUtil.executeQuery(
                    "SELECT COUNT(*) AS bill_count, ISNULL(SUM(total), 0) AS revenue " +
                            "FROM bills " +
                            "WHERE CAST(created_at AS DATE) = CAST(DATEADD(DAY,-1,GETDATE()) AS DATE) " +
                            "  AND status <> 'cancel'"
            );
            if (rs != null && rs.next()) {
                yesterdayBillCount = rs.getInt("bill_count");
                yesterdayRevenue   = rs.getDouble("revenue");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // ── 3. Pending bills count ────────────────────────────────────────
        int pendingCount = 0;
        try {
            ResultSet rs = JdbcUtil.executeQuery(
                    "SELECT COUNT(*) FROM bills WHERE status = 'waiting'"
            );
            if (rs != null && rs.next()) pendingCount = rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }

        // ── 4. Active staff count ─────────────────────────────────────────
        int activeStaff = 0;
        int totalStaff  = 0;
        try {
            ResultSet rs = JdbcUtil.executeQuery(
                    "SELECT " +
                            "  SUM(CASE WHEN active = 1 THEN 1 ELSE 0 END) AS active_count, " +
                            "  COUNT(*) AS total_count " +
                            "FROM users WHERE role = 0"
            );
            if (rs != null && rs.next()) {
                activeStaff = rs.getInt("active_count");
                totalStaff  = rs.getInt("total_count");
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // ── 5. Last 5 bills for the recent-orders panel ───────────────────
        List<Bill> recentBills = billDAO.findAllPaged(1, 5);

        // Attach staff name to each bill by joining via userId
        // We pass userDAO so the JSP can call it — simpler: build a map here
        java.util.Map<Integer, String> staffNames = new java.util.HashMap<>();
        for (Bill b : recentBills) {
            if (!staffNames.containsKey(b.getUserId())) {
                User u = userDAO.findById(b.getUserId());
                staffNames.put(b.getUserId(), u != null ? u.getFullName() : "—");
            }
        }

        // ── 6. Revenue for the last 7 days (for bar chart) ───────────────
        // Returns rows: day_label, revenue  (most recent day last)
        long[] chartRevenue = new long[7];
        String[] chartLabels = new String[7];
        try {
            ResultSet rs = JdbcUtil.executeQuery(
                    "SELECT TOP 7 " +
                            "  CAST(created_at AS DATE) AS day, " +
                            "  ISNULL(SUM(total), 0) AS revenue " +
                            "FROM bills " +
                            "WHERE status <> 'cancel' " +
                            "  AND created_at >= CAST(DATEADD(DAY,-6,GETDATE()) AS DATE) " +
                            "GROUP BY CAST(created_at AS DATE) " +
                            "ORDER BY day ASC"
            );
            // Pre-fill with zeros for missing days
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.format.DateTimeFormatter labelFmt =
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM");
            for (int i = 0; i < 7; i++) {
                chartLabels[i]  = today.minusDays(6 - i).format(labelFmt);
                chartRevenue[i] = 0;
            }
            while (rs != null && rs.next()) {
                java.sql.Date d = rs.getDate("day");
                java.time.LocalDate ld = d.toLocalDate();
                long gap = java.time.temporal.ChronoUnit.DAYS.between(today.minusDays(6), ld);
                if (gap >= 0 && gap < 7) {
                    chartRevenue[(int) gap] = rs.getLong("revenue");
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // ── 7. Put everything in request scope ────────────────────────────
        request.setAttribute("todayRevenue",      todayRevenue);
        request.setAttribute("todayBillCount",    todayBillCount);
        request.setAttribute("yesterdayRevenue",  yesterdayRevenue);
        request.setAttribute("yesterdayBillCount",yesterdayBillCount);
        request.setAttribute("pendingCount",      pendingCount);
        request.setAttribute("activeStaff",       activeStaff);
        request.setAttribute("totalStaff",        totalStaff);
        request.setAttribute("recentBills",       recentBills);
        request.setAttribute("staffNames",        staffNames);
        request.setAttribute("chartRevenue",      chartRevenue);
        request.setAttribute("chartLabels",       chartLabels);

        request.getRequestDispatcher("/WEB-INF/views/manager/dashboard/index.jsp")
                .forward(request, response);
    }
}