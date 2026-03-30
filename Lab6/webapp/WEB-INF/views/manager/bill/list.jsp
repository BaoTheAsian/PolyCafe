<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Hóa đơn — PolyCoffee</title>
  <c:set var="activeNav" value="bills" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <div class="pc-page-title">Quản lý hóa đơn</div>
      <div class="pc-page-sub">Xem, duyệt và hủy đơn hàng</div>
    </div>
  </div>

  <c:if test="${not empty sessionScope.message}">
    <div class="pc-alert success">${sessionScope.message}</div>
    <c:remove var="message" scope="session"/>
  </c:if>

  <div class="pc-card">
    <div style="padding:0;">
      <table class="pc-table">
        <thead>
          <tr>
            <th style="width:56px;">ID</th>
            <th>Mã hóa đơn</th>
            <th style="width:110px;">Nhân viên</th>
            <th style="width:150px;">Ngày tạo</th>
            <th style="width:120px;">Tổng tiền</th>
            <th style="width:110px;">Trạng thái</th>
            <th style="width:150px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="b" items="${bills}">
            <tr>
              <td style="color:rgba(44,24,16,.4);font-size:12px;">#${b.id}</td>
              <td style="font-weight:500;font-size:13px;">${b.code}</td>
              <td style="color:rgba(44,24,16,.55);">${b.userId}</td>
              <td style="color:rgba(44,24,16,.55);font-size:12px;">
                <fmt:formatDate value="${b.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
              </td>
              <td style="font-weight:500;"><fmt:formatNumber value="${b.total}" pattern="#,###"/>đ</td>
              <td>
                <span class="pc-pill ${b.status}">
                  <c:choose>
                    <c:when test="${b.status eq 'finish'}">Hoàn tất</c:when>
                    <c:when test="${b.status eq 'cancel'}">Đã hủy</c:when>
                    <c:otherwise>Chờ xử lý</c:otherwise>
                  </c:choose>
                </span>
              </td>
              <td>
                <div style="display:flex;gap:6px;">
                  <a href="?action=detail&id=${b.id}" class="pc-btn ghost sm">Chi tiết</a>
                  <c:if test="${b.status ne 'cancel'}">
                    <a href="?action=cancel&id=${b.id}" class="pc-btn danger sm"
                       onclick="return confirm('Hủy hóa đơn #${b.id}?')">Hủy</a>
                  </c:if>
                </div>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty bills}">
            <tr><td colspan="7" style="text-align:center;padding:32px;color:rgba(44,24,16,.4);">Chưa có hóa đơn nào</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>

  <c:if test="${totalPages > 1}">
    <div class="pc-pagination">
      <c:forEach begin="1" end="${totalPages}" var="i">
        <a href="?page=${i}" class="pc-page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
      </c:forEach>
    </div>
  </c:if>
</main>
</body>
</html>
