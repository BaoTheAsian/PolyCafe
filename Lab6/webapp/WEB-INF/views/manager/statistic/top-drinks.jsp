<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <title>Top 5 bán chạy — PolyCoffee</title>
  <c:set var="activeNav" value="statistics" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <div class="pc-page-title">Top 5 bán chạy nhất</div>
      <div class="pc-page-sub">Đồ uống được gọi nhiều nhất trong kỳ</div>
    </div>
    <a href="${pageContext.request.contextPath}/manager/statistics?action=revenue" class="pc-btn ghost">
      Xem doanh thu →
    </a>
  </div>

  <%-- Date filter --%>
  <div class="pc-card" style="margin-bottom:20px;">
    <div class="pc-card-bd" style="padding:16px 20px;">
      <form method="get" style="display:flex;gap:12px;align-items:flex-end;flex-wrap:wrap;">
        <input type="hidden" name="action" value="top-drinks">
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

  <div class="pc-card">
    <div style="padding:0;">
      <table class="pc-table">
        <thead>
          <tr>
            <th style="width:50px;text-align:center;">Hạng</th>
            <th>Tên đồ uống</th>
            <th style="width:120px;">Đơn giá</th>
            <th style="width:120px;">Số lượng bán</th>
            <th style="width:140px;text-align:right;">Doanh thu</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="d" items="${topDrinks}" varStatus="i">
            <tr>
              <td style="text-align:center;">
                <c:choose>
                  <c:when test="${i.index == 0}">
                    <div style="width:26px;height:26px;border-radius:50%;background:rgba(201,148,58,.15);color:var(--gold);font-size:13px;font-weight:500;display:flex;align-items:center;justify-content:center;margin:0 auto;">1</div>
                  </c:when>
                  <c:when test="${i.index == 1}">
                    <div style="width:26px;height:26px;border-radius:50%;background:rgba(44,24,16,.07);color:var(--roast);font-size:13px;font-weight:500;display:flex;align-items:center;justify-content:center;margin:0 auto;">2</div>
                  </c:when>
                  <c:when test="${i.index == 2}">
                    <div style="width:26px;height:26px;border-radius:50%;background:rgba(200,149,108,.1);color:var(--latte);font-size:13px;font-weight:500;display:flex;align-items:center;justify-content:center;margin:0 auto;">3</div>
                  </c:when>
                  <c:otherwise>
                    <span style="font-size:13px;color:rgba(44,24,16,.4);">${i.index + 1}</span>
                  </c:otherwise>
                </c:choose>
              </td>
              <td style="font-weight:500;">${d.name}</td>
              <td><fmt:formatNumber value="${d.price}" pattern="#,###"/>đ</td>
              <td>
                <div style="display:flex;align-items:center;gap:8px;">
                  <div style="flex:1;height:6px;background:var(--steam);border-radius:3px;overflow:hidden;max-width:80px;">
                    <div style="height:100%;background:var(--espresso);border-radius:3px;width:${(d.totalQuantity / topDrinks[0].totalQuantity) * 100}%;"></div>
                  </div>
                  <span style="font-size:13px;font-weight:500;">${d.totalQuantity}</span>
                </div>
              </td>
              <td style="text-align:right;font-weight:500;">
                <fmt:formatNumber value="${d.totalRevenue}" pattern="#,###"/>đ
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty topDrinks}">
            <tr><td colspan="5" style="text-align:center;padding:32px;color:rgba(44,24,16,.4);">Không có dữ liệu trong kỳ này</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</main>
</body>
</html>
