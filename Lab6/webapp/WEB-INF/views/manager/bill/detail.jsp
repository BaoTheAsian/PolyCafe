<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <title>Chi tiết hóa đơn — PolyCoffee</title>
  <c:set var="activeNav" value="bills" scope="request"/>
  <style>
    @media print {
      .pc-sidebar,.pc-topbar,.no-print { display:none !important; }
      .pc-main { margin-left:0 !important; padding:20px !important; }
      .receipt { max-width:320px; margin:0 auto; font-family:monospace; }
      .receipt-divider { border:none; border-top:1px dashed #333; margin:8px 0; }
    }
  </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar no-print">
    <div>
      <a href="${pageContext.request.contextPath}/manager/bills" class="pc-back">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.5">
          <path d="M9 11L5 7l4-4"/>
        </svg>
        Quay lại hóa đơn
      </a>
      <div class="pc-page-title" style="margin-top:8px;">Chi tiết hóa đơn</div>
    </div>
    <button onclick="window.print()" class="pc-btn ghost no-print">
      <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.4">
        <path d="M3.5 4V1.5h7V4M3.5 10H2a.5.5 0 01-.5-.5v-5A.5.5 0 012 4h10a.5.5 0 01.5.5v5a.5.5 0 01-.5.5h-1.5"/>
        <rect x="3.5" y="8" width="7" height="4.5" rx=".5"/>
      </svg>
      In hóa đơn
    </button>
  </div>

  <div style="display:grid;grid-template-columns:1fr 300px;gap:20px;align-items:start;">

    <%-- Items table --%>
    <div class="pc-card">
      <div class="pc-card-hd">
        <div class="pc-card-title">Danh sách món</div>
        <span style="font-size:12px;color:rgba(44,24,16,.4);">${details.size()} món</span>
      </div>
      <div style="padding:0;">
        <table class="pc-table">
          <thead>
            <tr>
              <th style="width:36px;">#</th>
              <th>Tên đồ uống</th>
              <th style="width:50px;">Size</th>
              <th style="width:110px;">Đơn giá</th>
              <th style="width:60px;text-align:center;">SL</th>
              <th style="width:120px;text-align:right;">Thành tiền</th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="dt" items="${details}" varStatus="i">
              <tr>
                <td style="color:rgba(44,24,16,.4);">${i.index + 1}</td>
                <td>
                  <div style="font-weight:500;">${dt.drinkName}</div>
                  <c:if test="${not empty dt.note}">
                    <div style="font-size:11px;color:rgba(44,24,16,.45);">${dt.note}</div>
                  </c:if>
                </td>
                <td style="text-align:center;">
                  <span style="font-size:11px;padding:1px 7px;border-radius:20px;
                               background:rgba(44,24,16,.06);font-weight:500;">${dt.size}</span>
                </td>
                <td><fmt:formatNumber value="${dt.price}" pattern="#,###"/>đ</td>
                <td style="text-align:center;">×${dt.quantity}</td>
                <td style="text-align:right;font-weight:500;">
                  <fmt:formatNumber value="${dt.quantity * dt.price}" pattern="#,###"/>đ
                </td>
              </tr>
            </c:forEach>
          </tbody>
          <tfoot>
            <tr>
              <td colspan="5" style="text-align:right;font-weight:500;padding:12px 14px;">Tổng cộng:</td>
              <td style="text-align:right;font-weight:500;font-size:15px;padding:12px 14px;">
                <fmt:formatNumber value="${bill.total}" pattern="#,###"/>đ
              </td>
            </tr>
          </tfoot>
        </table>
      </div>
    </div>

    <%-- Bill info card --%>
    <div class="pc-card">
      <div class="pc-card-hd">
        <div class="pc-card-title">Thông tin hóa đơn</div>
      </div>
      <div class="pc-card-bd">
        <table style="width:100%;font-size:13px;border-collapse:collapse;">
          <tr>
            <td style="color:rgba(44,24,16,.45);padding:7px 0;border-bottom:1px solid var(--steam);width:45%;">Mã HĐ</td>
            <td style="font-weight:500;padding:7px 0;border-bottom:1px solid var(--steam);">${bill.code}</td>
          </tr>
          <tr>
            <td style="color:rgba(44,24,16,.45);padding:7px 0;border-bottom:1px solid var(--steam);">Nhân viên</td>
            <td style="padding:7px 0;border-bottom:1px solid var(--steam);">${staffName}</td>
          </tr>
          <tr>
            <td style="color:rgba(44,24,16,.45);padding:7px 0;border-bottom:1px solid var(--steam);">Thanh toán</td>
            <td style="padding:7px 0;border-bottom:1px solid var(--steam);">
              <c:choose>
                <c:when test="${bill.paymentMethod eq 'momo'}">MoMo</c:when>
                <c:when test="${bill.paymentMethod eq 'zalopay'}">ZaloPay</c:when>
                <c:when test="${bill.paymentMethod eq 'card'}">Thẻ ngân hàng</c:when>
                <c:otherwise>Tiền mặt</c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td style="color:rgba(44,24,16,.45);padding:7px 0;border-bottom:1px solid var(--steam);">Ngày tạo</td>
            <td style="padding:7px 0;border-bottom:1px solid var(--steam);">
              <fmt:formatDate value="${bill.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
            </td>
          </tr>
          <tr>
            <td style="color:rgba(44,24,16,.45);padding:7px 0;border-bottom:1px solid var(--steam);">Trạng thái</td>
            <td style="padding:7px 0;border-bottom:1px solid var(--steam);">
              <span class="pc-pill ${bill.status}">
                <c:choose>
                  <c:when test="${bill.status eq 'finish'}">Hoàn tất</c:when>
                  <c:when test="${bill.status eq 'cancel'}">Đã hủy</c:when>
                  <c:otherwise>Chờ xử lý</c:otherwise>
                </c:choose>
              </span>
            </td>
          </tr>
          <tr>
            <td style="color:rgba(44,24,16,.45);padding:10px 0 0;">Tổng tiền</td>
            <td style="padding:10px 0 0;font-size:18px;font-family:'Playfair Display',serif;font-weight:500;">
              <fmt:formatNumber value="${bill.total}" pattern="#,###"/>đ
            </td>
          </tr>
        </table>

        <c:if test="${bill.status ne 'cancel'}">
          <div style="margin-top:16px;padding-top:14px;border-top:1px solid var(--steam);" class="no-print">
            <a href="${pageContext.request.contextPath}/manager/bills?action=cancel&id=${bill.id}"
               class="pc-btn danger" style="width:100%;justify-content:center;"
               onclick="return confirm('Hủy hóa đơn này?')">Hủy hóa đơn</a>
          </div>
        </c:if>
      </div>
    </div>
  </div>
</main>
</body>
</html>
