<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <title>Thống kê doanh thu — PolyCoffee</title>
  <c:set var="activeNav" value="statistics" scope="request"/>
  <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <div class="pc-page-title">Thống kê doanh thu</div>
      <div class="pc-page-sub">Biểu đồ doanh thu theo ngày</div>
    </div>
    <a href="${pageContext.request.contextPath}/manager/statistics?action=top-drinks" class="pc-btn ghost">
      Xem Top 5 bán chạy →
    </a>
  </div>

  <%-- Date filter --%>
  <div class="pc-card" style="margin-bottom:20px;">
    <div class="pc-card-bd" style="padding:16px 20px;">
      <form method="get" style="display:flex;gap:12px;align-items:flex-end;flex-wrap:wrap;">
        <input type="hidden" name="action" value="revenue">
        <div>
          <label class="pc-label">Từ ngày</label>
          <input type="date" name="fromDate" class="pc-input" value="${fromDate}" style="width:170px;">
        </div>
        <div>
          <label class="pc-label">Đến ngày</label>
          <input type="date" name="toDate" class="pc-input" value="${toDate}" style="width:170px;">
        </div>
        <button type="submit" class="pc-btn latte">Xem thống kê</button>
      </form>
    </div>
  </div>

  <div style="display:grid;grid-template-columns:1fr 340px;gap:20px;align-items:start;">

    <%-- Chart --%>
    <div class="pc-card">
      <div class="pc-card-hd">
        <div class="pc-card-title">Biểu đồ doanh thu</div>
      </div>
      <div class="pc-card-bd">
        <canvas id="revenueChart" height="100"></canvas>
      </div>
    </div>

    <%-- Data table --%>
    <div class="pc-card">
      <div class="pc-card-hd">
        <div class="pc-card-title">Chi tiết theo ngày</div>
      </div>
      <div style="padding:0;">
        <table class="pc-table">
          <thead>
            <tr>
              <th>Ngày</th>
              <th style="width:70px;text-align:center;">HĐ</th>
              <th style="width:120px;text-align:right;">Doanh thu</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="r" items="${revenues}">
              <tr>
                <td style="font-size:12px;">${r.billDate}</td>
                <td style="text-align:center;">${r.totalBills}</td>
                <td style="text-align:right;font-weight:500;">
                  <fmt:formatNumber value="${r.totalRevenue}" pattern="#,###"/>đ
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty revenues}">
              <tr><td colspan="3" style="text-align:center;padding:24px;color:rgba(44,24,16,.4);">Không có dữ liệu</td></tr>
            </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</main>

<script>
  const ctx = document.getElementById('revenueChart').getContext('2d');
  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: ${chartLabels != null ? chartLabels : '[]'},
      datasets: [{
        label: 'Doanh thu (VNĐ)',
        data: ${chartData != null ? chartData : '[]'},
        backgroundColor: 'rgba(44,24,16,0.75)',
        borderRadius: 6,
        borderSkipped: false,
      }]
    },
    options: {
      responsive: true,
      plugins: {
        legend: { display: false },
        tooltip: {
          callbacks: {
            label: ctx => ctx.parsed.y.toLocaleString('vi-VN') + 'đ'
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          grid: { color: 'rgba(44,24,16,0.06)' },
          ticks: { callback: v => (v/1000).toLocaleString() + 'k', font: { family: 'DM Sans' } }
        },
        x: {
          grid: { display: false },
          ticks: { font: { family: 'DM Sans', size: 11 } }
        }
      }
    }
  });
</script>
</body>
</html>
