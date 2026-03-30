package servlet;

import dao.StatisticDAO;
import entity.BestSellingDrink;
import entity.Revenue;
import utils.ParamUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/manager/statistics")
public class StatisticServlet extends HttpServlet {

    private StatisticDAO statisticDAO;

    @Override
    public void init() throws ServletException {
        statisticDAO = new StatisticDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = ParamUtil.getString(request, "action", "top-drinks");
        String fromDate = ParamUtil.getString(request, "fromDate", "");
        String toDate = ParamUtil.getString(request, "toDate", "");

        request.setAttribute("fromDate", fromDate);
        request.setAttribute("toDate", toDate);

        switch (action) {
            case "revenue":
                List<Revenue> revenues = statisticDAO.getRevenueByDay(fromDate, toDate);
                request.setAttribute("revenues", revenues);

                // Chuẩn bị dữ liệu JSON cho biểu đồ
                StringBuilder labels = new StringBuilder("[");
                StringBuilder data = new StringBuilder("[");
                for (int i = 0; i < revenues.size(); i++) {
                    Revenue r = revenues.get(i);
                    labels.append("\"").append(r.getBillDate()).append("\"");
                    data.append(r.getTotalRevenue());
                    if (i < revenues.size() - 1) {
                        labels.append(",");
                        data.append(",");
                    }
                }
                labels.append("]");
                data.append("]");
                request.setAttribute("chartLabels", labels.toString());
                request.setAttribute("chartData", data.toString());

                request.getRequestDispatcher("/WEB-INF/views/manager/statistic/revenue.jsp")
                        .forward(request, response);
                break;

            default: // top-drinks
                List<BestSellingDrink> topDrinks = statisticDAO.getTop5BestSellingDrinks(fromDate, toDate);
                request.setAttribute("topDrinks", topDrinks);
                request.getRequestDispatcher("/WEB-INF/views/manager/statistic/top-drinks.jsp")
                        .forward(request, response);
                break;
        }
    }
}
