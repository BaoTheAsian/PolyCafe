<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>POS — PolyCoffee</title>
  <c:set var="activeNav" value="pos" scope="request"/>
  <style>
    /* POS-specific styles */
    .pos-layout { display:grid; grid-template-columns:1fr 360px; gap:20px; height:calc(100vh - 110px); }
    .drinks-panel { overflow-y:auto; padding-right:4px; }
    .drinks-panel::-webkit-scrollbar { width:4px; }
    .drinks-panel::-webkit-scrollbar-track { background:transparent; }
    .drinks-panel::-webkit-scrollbar-thumb { background:var(--steam); border-radius:4px; }
    .drink-grid { display:grid; grid-template-columns:repeat(auto-fill, minmax(150px,1fr)); gap:12px; }
    .drink-card {
      background:#fff; border:1px solid var(--steam); border-radius:12px;
      overflow:hidden; cursor:pointer; transition:all .18s;
    }
    .drink-card:hover { transform:translateY(-2px); box-shadow:0 6px 18px rgba(44,24,16,.09); border-color:var(--latte); }
    .drink-img { width:100%; height:100px; object-fit:cover; }
    .drink-img-placeholder {
      width:100%; height:100px; background:var(--foam);
      display:flex; align-items:center; justify-content:center;
    }
    .drink-info { padding:10px 12px 12px; }
    .drink-name { font-size:13px; font-weight:500; margin-bottom:4px; line-height:1.3; }
    .drink-price { font-size:12px; color:var(--latte); font-weight:500; }
    .add-btn {
      width:100%; margin-top:8px; padding:6px; border:1px solid var(--steam);
      border-radius:7px; background:transparent; font-family:'DM Sans',sans-serif;
      font-size:12px; color:var(--espresso); cursor:pointer; transition:all .14s;
    }
    .add-btn:hover { background:var(--espresso); color:var(--foam); border-color:var(--espresso); }

    /* Order panel */
    .order-panel {
      background:#fff; border:1px solid var(--steam); border-radius:14px;
      display:flex; flex-direction:column; overflow:hidden;
    }
    .order-header { padding:16px 18px 14px; border-bottom:1px solid var(--steam); }
    .order-title { font-family:'Playfair Display',serif; font-size:15px; font-weight:500; }
    .order-code { font-size:11px; color:rgba(44,24,16,.4); margin-top:2px; }
    .order-items { flex:1; overflow-y:auto; padding:8px 0; }
    .order-items::-webkit-scrollbar { width:3px; }
    .order-items::-webkit-scrollbar-thumb { background:var(--steam); border-radius:3px; }
    .order-row {
      display:flex; align-items:center; gap:10px; padding:10px 18px;
      border-bottom:1px solid var(--steam); transition:background .12s;
    }
    .order-row:last-child { border-bottom:none; }
    .order-row:hover { background:var(--foam); }
    .order-drink-name { font-size:13px; font-weight:500; flex:1; min-width:0; }
    .order-drink-price { font-size:11px; color:rgba(44,24,16,.45); margin-top:1px; }
    .qty-controls { display:flex; align-items:center; gap:4px; flex-shrink:0; }
    .qty-btn {
      width:24px; height:24px; border-radius:6px; border:1px solid var(--steam);
      background:transparent; cursor:pointer; display:flex; align-items:center; justify-content:center;
      font-size:14px; color:var(--espresso); transition:all .12s; font-family:'DM Sans',sans-serif;
    }
    .qty-btn:hover { background:var(--espresso); color:var(--foam); border-color:var(--espresso); }
    .qty-btn.remove:hover { background:var(--warm-red); border-color:var(--warm-red); }
    .qty-num { font-size:13px; font-weight:500; min-width:20px; text-align:center; }
    .order-line-total { font-size:13px; font-weight:500; min-width:70px; text-align:right; }

    .order-footer { padding:16px 18px; border-top:1px solid var(--steam); }
    .order-total-row { display:flex; align-items:baseline; justify-content:space-between; margin-bottom:14px; }
    .order-total-label { font-size:13px; color:rgba(44,24,16,.5); }
    .order-total-value { font-family:'Playfair Display',serif; font-size:22px; font-weight:500; }
    .order-actions { display:flex; gap:8px; }
    .order-actions form { flex:1; }
    .order-actions button { width:100%; }

    .empty-order {
      flex:1; display:flex; flex-direction:column; align-items:center; justify-content:center;
      padding:32px; text-align:center; color:rgba(44,24,16,.35);
    }
    .empty-order svg { margin-bottom:12px; opacity:.3; }
    .empty-order p { font-size:13px; }
  </style>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar" style="margin-bottom:18px;">
    <div>
      <div class="pc-page-title">POS — Bán hàng</div>
      <div class="pc-page-sub">Tạo đơn và thanh toán nhanh</div>
    </div>
    <c:if test="${not empty bill}">
      <div style="font-size:13px;color:rgba(44,24,16,.45);">
        Đơn hiện tại: <strong style="color:var(--espresso);">${bill.code}</strong>
      </div>
    </c:if>
  </div>

  <c:if test="${not empty sessionScope.message}">
    <div class="pc-alert success">${sessionScope.message}</div>
    <c:remove var="message" scope="session"/>
  </c:if>

  <div class="pos-layout">

    <%-- Left: Drink grid --%>
    <div class="drinks-panel">
      <div class="drink-grid">
        <c:forEach var="d" items="${drinks}">
          <div class="drink-card">
            <c:choose>
              <c:when test="${not empty d.image}">
                <img class="drink-img" src="${pageContext.request.contextPath}/uploads/${d.image}" alt="${d.name}">
              </c:when>
              <c:otherwise>
                <div class="drink-img-placeholder">
                  <svg width="32" height="32" viewBox="0 0 32 32" fill="none" stroke="rgba(44,24,16,.18)" stroke-width="1.5"><path d="M6 10h20L23 24H9L6 10z"/><path d="M11 10V8a5 5 0 0110 0v2"/></svg>
                </div>
              </c:otherwise>
            </c:choose>
            <div class="drink-info">
              <div class="drink-name">${d.name}</div>
              <div class="drink-price"><fmt:formatNumber value="${d.price}" pattern="#,###"/>đ</div>
              <form method="post" action="${pageContext.request.contextPath}/employee/pos/init">
                <input type="hidden" name="drinkId" value="${d.id}">
                <input type="hidden" name="price" value="${d.price}">
                <button type="submit" class="add-btn">+ Thêm vào đơn</button>
              </form>
            </div>
          </div>
        </c:forEach>
      </div>
    </div>

    <%-- Right: Order panel --%>
    <div class="order-panel">
      <div class="order-header">
        <div class="order-title">Đơn hàng hiện tại</div>
        <div class="order-code">
          <c:choose>
            <c:when test="${not empty bill}">#${bill.code}</c:when>
            <c:otherwise>Chưa có đơn nào</c:otherwise>
          </c:choose>
        </div>
      </div>

      <c:choose>
        <c:when test="${not empty details}">
          <div class="order-items">
            <c:forEach var="dt" items="${details}">
              <div class="order-row">
                <div style="flex:1;min-width:0;">
                  <div class="order-drink-name">${dt.drinkId}</div>
                  <div class="order-drink-price"><fmt:formatNumber value="${dt.price}" pattern="#,###"/>đ / ly</div>
                </div>
                <div class="qty-controls">
                  <form method="post" action="${pageContext.request.contextPath}/employee/pos/update-quantity">
                    <input type="hidden" name="detailId" value="${dt.id}">
                    <input type="hidden" name="billId" value="${bill.id}">
                    <input type="hidden" name="quantityAction" value="decrease">
                    <button type="submit" class="qty-btn">−</button>
                  </form>
                  <span class="qty-num">${dt.quantity}</span>
                  <form method="post" action="${pageContext.request.contextPath}/employee/pos/update-quantity">
                    <input type="hidden" name="detailId" value="${dt.id}">
                    <input type="hidden" name="billId" value="${bill.id}">
                    <input type="hidden" name="quantityAction" value="increase">
                    <button type="submit" class="qty-btn">+</button>
                  </form>
                  <form method="post" action="${pageContext.request.contextPath}/employee/pos/update-quantity">
                    <input type="hidden" name="detailId" value="${dt.id}">
                    <input type="hidden" name="billId" value="${bill.id}">
                    <input type="hidden" name="quantityAction" value="remove">
                    <button type="submit" class="qty-btn remove" title="Xóa">×</button>
                  </form>
                </div>
                <div class="order-line-total">
                  <fmt:formatNumber value="${dt.quantity * dt.price}" pattern="#,###"/>đ
                </div>
              </div>
            </c:forEach>
          </div>

          <div class="order-footer">
            <div class="order-total-row">
              <div class="order-total-label">Tổng cộng</div>
              <div class="order-total-value"><fmt:formatNumber value="${bill.total}" pattern="#,###"/>đ</div>
            </div>
            <div class="order-actions">
              <form method="post" action="${pageContext.request.contextPath}/employee/pos/checkout">
                <input type="hidden" name="billId" value="${bill.id}">
                <button type="submit" class="pc-btn primary" onclick="return confirm('Xác nhận thanh toán?')"
                        style="width:100%;justify-content:center;padding:10px;">
                  Thanh toán
                </button>
              </form>
              <form method="post" action="${pageContext.request.contextPath}/employee/pos/cancel">
                <input type="hidden" name="billId" value="${bill.id}">
                <button type="submit" class="pc-btn danger" onclick="return confirm('Hủy đơn hàng này?')"
                        style="width:100%;justify-content:center;padding:10px;">
                  Hủy đơn
                </button>
              </form>
            </div>
          </div>
        </c:when>
        <c:otherwise>
          <div class="empty-order">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none" stroke="currentColor" stroke-width="1.5">
              <path d="M8 14h32L35 36H13L8 14z"/>
              <path d="M17 14V11a7 7 0 0114 0v3"/>
              <path d="M20 24h8"/>
            </svg>
            <p>Chưa có đơn hàng<br>Chọn đồ uống để bắt đầu</p>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</main>
</body>
</html>
