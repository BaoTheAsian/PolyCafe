<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <title>Hóa đơn — PolyCoffee</title>
  <c:set var="activeNav" value="bills" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <div class="pc-page-title">Quản lý hóa đơn</div>
      <div class="pc-page-sub">Xem, lọc và hủy đơn hàng</div>
    </div>
  </div>

  <c:if test="${not empty sessionScope.message}">
    <div class="pc-alert success">${sessionScope.message}</div>
    <c:remove var="message" scope="session"/>
  </c:if>
  <c:if test="${not empty sessionScope.error}">
    <div class="pc-alert danger">${sessionScope.error}</div>
    <c:remove var="error" scope="session"/>
  </c:if>

  <%-- Search / filter bar --%>
  <form method="get" action="${pageContext.request.contextPath}/manager/bills"
        style="background:#fff;border:1px solid var(--steam);border-radius:12px;
               padding:16px 20px;margin-bottom:18px;display:flex;gap:10px;flex-wrap:wrap;align-items:flex-end;">
    <div>
      <label class="pc-label">Từ ngày</label>
      <input type="date" name="fromDate" class="pc-input" value="${fromDate}" style="width:155px;">
    </div>
    <div>
      <label class="pc-label">Đến ngày</label>
      <input type="date" name="toDate" class="pc-input" value="${toDate}" style="width:155px;">
    </div>
    <div>
      <label class="pc-label">Trạng thái</label>
      <select name="status" class="pc-select" style="width:145px;">
        <option value=""        ${empty status          ? 'selected' : ''}>Tất cả</option>
        <option value="waiting" ${'waiting' eq status   ? 'selected' : ''}>Chờ xử lý</option>
        <option value="finish"  ${'finish'  eq status   ? 'selected' : ''}>Hoàn tất</option>
        <option value="cancel"  ${'cancel'  eq status   ? 'selected' : ''}>Đã hủy</option>
      </select>
    </div>
    <div>
      <label class="pc-label">Mã / từ khóa</label>
      <input type="text" name="keyword" class="pc-input" value="${keyword}" placeholder="HD00123…" style="width:160px;">
    </div>
    <button type="submit" class="pc-btn latte">Tìm kiếm</button>
    <a href="${pageContext.request.contextPath}/manager/bills" class="pc-btn ghost">Xóa bộ lọc</a>
  </form>

  <div class="pc-card">
    <div style="padding:0;">
      <table class="pc-table">
        <thead>
          <tr>
            <th style="width:56px;">ID</th>
            <th>Mã hóa đơn</th>
            <th style="width:140px;">Nhân viên</th>
            <th style="width:90px;">Thanh toán</th>
            <th style="width:150px;">Ngày tạo</th>
            <th style="width:120px;">Tổng tiền</th>
            <th style="width:110px;">Trạng thái</th>
            <th style="width:140px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="b" items="${bills}">
            <tr>
              <td style="color:rgba(44,24,16,.4);font-size:12px;">#${b.id}</td>
              <td style="font-weight:500;font-size:13px;">${b.code}</td>
              <td style="color:rgba(44,24,16,.55);">${staffNames[b.userId]}</td>
              <td>
                <span style="font-size:11px;padding:2px 8px;border-radius:20px;
                             background:rgba(44,24,16,.06);color:rgba(44,24,16,.6);">
                  <c:choose>
                    <c:when test="${b.paymentMethod eq 'momo'}">MoMo</c:when>
                    <c:when test="${b.paymentMethod eq 'zalopay'}">ZaloPay</c:when>
                    <c:when test="${b.paymentMethod eq 'card'}">Thẻ</c:when>
                    <c:otherwise>Tiền mặt</c:otherwise>
                  </c:choose>
                </span>
              </td>
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
                <div style="display:flex;gap:5px;">
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
            <tr><td colspan="8" style="text-align:center;padding:32px;color:rgba(44,24,16,.4);">
              Không tìm thấy hóa đơn nào
            </td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>

  <c:if test="${totalPages > 1}">
    <div class="pc-pagination">
      <c:forEach begin="1" end="${totalPages}" var="i">
        <a href="?fromDate=${fromDate}&toDate=${toDate}&status=${status}&keyword=${keyword}&page=${i}"
           class="pc-page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
      </c:forEach>
    </div>
  </c:if>
</main>
</body>
</html>
