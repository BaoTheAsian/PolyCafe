package servlet;

import dao.BillDAO;
import dao.DrinkDAO;
import dao.UserDAO;
import entity.Bill;
import entity.Drink;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = "/manager/dashboard")
public class DashboardServlet extends HttpServlet {

    private BillDAO billDAO;
    private UserDAO userDAO;
    private DrinkDAO drinkDAO;

    @Override
    public void init() throws ServletException {
        billDAO  = new BillDAO();
        userDAO  = new UserDAO();
        drinkDAO = new DrinkDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Both managers and employees can reach this page.
        // Employees see only the POS section; manager stats are loaded regardless
        // but hidden in the JSP for non-managers.

        // ── 1. Today's revenue & bill count (finish only) ────────────────
        double todayRevenue   = 0;
        int    todayBillCount = 0;
        JdbcUtil.ResultSetHolder h1 = JdbcUtil.executeQuery(
                "SELECT COUNT(*) AS bill_count, ISNULL(SUM(total),0) AS revenue " +
                        "FROM bills WHERE CAST(created_at AS DATE)=CAST(GETDATE() AS DATE) AND status='finish'");
        try {
            if (h1 != null && h1.rs().next()) {
                todayBillCount = h1.rs().getInt("bill_count");
                todayRevenue   = h1.rs().getDouble("revenue");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h1); }

        // ── 2. Yesterday's revenue ────────────────────────────────────────
        double yesterdayRevenue   = 0;
        int    yesterdayBillCount = 0;
        JdbcUtil.ResultSetHolder h2 = JdbcUtil.executeQuery(
                "SELECT COUNT(*) AS bill_count, ISNULL(SUM(total),0) AS revenue " +
                        "FROM bills WHERE CAST(created_at AS DATE)=CAST(DATEADD(DAY,-1,GETDATE()) AS DATE) AND status='finish'");
        try {
            if (h2 != null && h2.rs().next()) {
                yesterdayBillCount = h2.rs().getInt("bill_count");
                yesterdayRevenue   = h2.rs().getDouble("revenue");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h2); }

        // ── 3. Pending bills ──────────────────────────────────────────────
        int pendingCount = 0;
        JdbcUtil.ResultSetHolder h3 = JdbcUtil.executeQuery(
                "SELECT COUNT(*) FROM bills WHERE status='waiting'");
        try {
            if (h3 != null && h3.rs().next()) pendingCount = h3.rs().getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h3); }

        // ── 4. Staff counts ───────────────────────────────────────────────
        int activeStaff = 0, totalStaff = 0;
        JdbcUtil.ResultSetHolder h4 = JdbcUtil.executeQuery(
                "SELECT SUM(CASE WHEN active=1 THEN 1 ELSE 0 END) AS active_count, " +
                        "COUNT(*) AS total_count FROM users WHERE role != 'manager'");
        try {
            if (h4 != null && h4.rs().next()) {
                activeStaff = h4.rs().getInt("active_count");
                totalStaff  = h4.rs().getInt("total_count");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h4); }

        // ── 5. Recent bills with staff names ──────────────────────────────
        List<Bill> recentBills = billDAO.findAllPaged(1, 5);
        Map<Integer, String> staffNames = new HashMap<>();
        for (Bill b : recentBills) {
            staffNames.computeIfAbsent(b.getUserId(), id -> {
                User u = userDAO.findById(id);
                return u != null ? u.getFullName() : "—";
            });
        }

        // ── 6. Last 7 days revenue for bar chart (finish only) ────────────
        long[]   chartRevenue = new long[7];
        String[] chartLabels  = new String[7];
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM");
        for (int i = 0; i < 7; i++) chartLabels[i] = today.minusDays(6 - i).format(fmt);

        JdbcUtil.ResultSetHolder h5 = JdbcUtil.executeQuery(
                "SELECT CAST(created_at AS DATE) AS day, ISNULL(SUM(total),0) AS revenue " +
                        "FROM bills WHERE status='finish' " +
                        "  AND created_at >= CAST(DATEADD(DAY,-6,GETDATE()) AS DATE) " +
                        "GROUP BY CAST(created_at AS DATE) ORDER BY day ASC");
        try {
            ResultSet rs = h5 != null ? h5.rs() : null;
            while (rs != null && rs.next()) {
                java.sql.Date d = rs.getDate("day");
                long gap = java.time.temporal.ChronoUnit.DAYS.between(today.minusDays(6), d.toLocalDate());
                if (gap >= 0 && gap < 7) chartRevenue[(int) gap] = rs.getLong("revenue");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { JdbcUtil.closeQuietly(h5); }

        // ── 7. Employee-specific data ─────────────────────────────────────
        User currentUser = AuthUtil.getUser(request);
        int currentUserId = currentUser != null ? currentUser.getId() : -1;

        // Their bills today (all statuses, most recent first)
        List<Bill> myTodayBills = billDAO.findBySql(
                "SELECT * FROM bills WHERE user_id = ? " +
                        "AND CAST(created_at AS DATE) = CAST(GETDATE() AS DATE) " +
                        "ORDER BY id DESC", currentUserId);

        // Their current waiting bill (active order)
        Bill myActiveBill = billDAO.findWaitingBill(currentUserId);

        // Their personal stats today
        int    myBillCount   = 0;
        double myRevenue     = 0;
        int    myFinishCount = 0;
        for (Bill b : myTodayBills) {
            myBillCount++;
            if ("finish".equals(b.getStatus())) {
                myFinishCount++;
                myRevenue += b.getTotal();
            }
        }

        // Top 6 active drinks for the menu preview
        List<Drink> menuDrinks = drinkDAO.findBySql(
                "SELECT TOP 6 * FROM drinks WHERE active = 1 ORDER BY id DESC");

        // ── 8. Bind to request ────────────────────────────────────────────
        request.setAttribute("todayRevenue",       todayRevenue);
        request.setAttribute("todayBillCount",     todayBillCount);
        request.setAttribute("yesterdayRevenue",   yesterdayRevenue);
        request.setAttribute("yesterdayBillCount", yesterdayBillCount);
        request.setAttribute("pendingCount",       pendingCount);
        request.setAttribute("activeStaff",        activeStaff);
        request.setAttribute("totalStaff",         totalStaff);
        request.setAttribute("recentBills",        recentBills);
        request.setAttribute("staffNames",         staffNames);
        request.setAttribute("chartRevenue",       chartRevenue);
        request.setAttribute("chartLabels",        chartLabels);
        // Employee-specific
        request.setAttribute("myTodayBills",       myTodayBills);
        request.setAttribute("myActiveBill",       myActiveBill);
        request.setAttribute("myBillCount",        myBillCount);
        request.setAttribute("myFinishCount",      myFinishCount);
        request.setAttribute("myRevenue",          myRevenue);
        request.setAttribute("menuDrinks",         menuDrinks);

        request.getRequestDispatcher("/WEB-INF/views/manager/dashboard/index.jsp")
                .forward(request, response);
    }
}