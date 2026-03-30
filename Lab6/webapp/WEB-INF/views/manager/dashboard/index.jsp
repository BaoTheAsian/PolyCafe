<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Dashboard — PolyCoffee</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    :root {
      --cream: #FAF6F0; --espresso: #2C1810; --latte: #C8956C;
      --foam: #F5EDE0;  --roast: #7B4A2D;    --mocha: #4A2C1A;
      --steam: #E8DDD0; --leaf: #3D7A5C;     --gold: #C9943A;
      --warm-red: #C04B3E;
    }
    body { font-family: 'DM Sans', sans-serif; background: var(--cream); color: var(--espresso); }

    /* ── Sidebar ── */
    .sidebar {
      position: fixed; left: 0; top: 0; bottom: 0; width: 220px;
      background: var(--espresso); display: flex; flex-direction: column; z-index: 100;
    }
    .logo-area { padding: 28px 24px 20px; border-bottom: 1px solid rgba(255,255,255,0.08); }
    .logo-cup {
      width: 36px; height: 36px; background: var(--latte);
      border-radius: 50% 50% 45% 45%;
      display: flex; align-items: center; justify-content: center;
      margin-bottom: 12px; position: relative;
    }
    .logo-cup::after {
      content:''; position: absolute; top: -6px; right: -4px;
      width: 10px; height: 10px; border: 2px solid var(--latte);
      border-radius: 0 50% 50% 0; border-left: none;
    }
    .logo-title { font-family:'Playfair Display',serif; font-size:18px; color:var(--foam); letter-spacing:.5px; }
    .logo-sub   { font-size:10px; color:rgba(255,255,255,.4); letter-spacing:2px; text-transform:uppercase; margin-top:2px; }
    .role-badge {
      margin: 14px 24px; display: inline-flex; align-items: center; gap: 6px;
      background: rgba(200,149,108,.15); border: 1px solid rgba(200,149,108,.25);
      color: var(--latte); font-size: 11px; padding: 5px 10px; border-radius: 20px;
    }
    .role-dot { width:6px; height:6px; background:var(--latte); border-radius:50%; animation: pulse 2s infinite; }
    @keyframes pulse { 0%,100%{opacity:1} 50%{opacity:.4} }
    nav { flex:1; padding: 8px 0; overflow-y: auto; }
    .nav-section { padding: 14px 24px 5px; font-size:9px; letter-spacing:2px; text-transform:uppercase; color:rgba(255,255,255,.25); }
    .nav-item {
      display: flex; align-items: center; gap: 10px; padding: 9px 24px;
      color: rgba(255,255,255,.55); font-size: 13.5px; cursor: pointer;
      border-left: 2px solid transparent; text-decoration: none; transition: all .15s;
    }
    .nav-item:hover  { color:rgba(255,255,255,.9); background:rgba(255,255,255,.04); border-left-color:rgba(200,149,108,.4); }
    .nav-item.active { color:var(--foam); background:rgba(200,149,108,.12); border-left-color:var(--latte); font-weight:500; }
    .nav-badge {
      margin-left:auto; background:var(--warm-red); color:#fff;
      font-size:10px; padding:1px 6px; border-radius:10px; min-width:18px; text-align:center;
    }
    .sidebar-footer { padding:16px 24px; border-top:1px solid rgba(255,255,255,.08); }
    .user-row { display:flex; align-items:center; gap:10px; }
    .avatar {
      width:32px; height:32px; border-radius:50%;
      background: linear-gradient(135deg, var(--latte), var(--roast));
      display:flex; align-items:center; justify-content:center;
      font-size:12px; font-weight:500; color:#fff; flex-shrink:0;
    }
    .user-name { font-size:13px; font-weight:500; color:var(--foam); white-space:nowrap; overflow:hidden; text-overflow:ellipsis; }
    .user-role { font-size:11px; color:rgba(255,255,255,.35); }
    .logout-btn { background:none; border:none; cursor:pointer; color:rgba(255,255,255,.3); padding:4px; transition:color .15s; display:flex; }
    .logout-btn:hover { color:var(--warm-red); }

    /* ── Main ── */
    .main { margin-left: 220px; padding: 32px 36px; min-height:100vh; }
    .topbar { display:flex; align-items:center; justify-content:space-between; margin-bottom:30px; }
    .page-heading { font-family:'Playfair Display',serif; font-size:26px; font-weight:500; }
    .page-sub { font-size:13px; color:rgba(44,24,16,.45); margin-top:2px; }
    .topbar-right { display:flex; align-items:center; gap:12px; }
    .time-chip {
      font-size:12px; color:rgba(44,24,16,.5);
      background:var(--foam); border:1px solid var(--steam);
      padding:6px 14px; border-radius:20px; display:flex; align-items:center; gap:6px;
    }
    .notif-btn {
      width:36px; height:36px; border-radius:50%;
      background:var(--foam); border:1px solid var(--steam);
      display:flex; align-items:center; justify-content:center; cursor:pointer; position:relative;
    }
    .notif-dot { position:absolute; top:7px; right:7px; width:7px; height:7px; border-radius:50%; background:var(--warm-red); border:1.5px solid var(--cream); }

    /* ── Stat Cards ── */
    .stats-grid { display:grid; grid-template-columns:repeat(4,1fr); gap:16px; margin-bottom:26px; }
    .stat-card {
      background:#fff; border:1px solid var(--steam); border-radius:14px; padding:20px;
      position:relative; overflow:hidden; cursor:pointer;
      transition: transform .2s, box-shadow .2s;
    }
    .stat-card:hover { transform:translateY(-2px); box-shadow:0 8px 24px rgba(44,24,16,.08); }
    .stat-card::before { content:''; position:absolute; top:0; left:0; right:0; height:3px; }
    .stat-card.revenue::before { background:var(--gold); }
    .stat-card.orders::before  { background:var(--latte); }
    .stat-card.staff::before   { background:var(--leaf); }
    .stat-card.pending::before { background:var(--warm-red); }
    .stat-icon { width:36px; height:36px; border-radius:10px; display:flex; align-items:center; justify-content:center; margin-bottom:14px; }
    .stat-card.revenue .stat-icon { background:rgba(201,148,58,.12); }
    .stat-card.orders  .stat-icon { background:rgba(200,149,108,.12); }
    .stat-card.staff   .stat-icon { background:rgba(61,122,92,.12); }
    .stat-card.pending .stat-icon { background:rgba(192,75,62,.12); }
    .stat-label { font-size:11px; color:rgba(44,24,16,.45); text-transform:uppercase; letter-spacing:1px; margin-bottom:6px; }
    .stat-value { font-size:26px; font-weight:300; font-family:'Playfair Display',serif; line-height:1; }
    .stat-change { font-size:12px; margin-top:8px; display:flex; align-items:center; gap:4px; }
    .stat-change.up      { color:var(--leaf); }
    .stat-change.down    { color:var(--warm-red); }
    .stat-change.neutral { color:rgba(44,24,16,.4); }

    /* ── Middle Row ── */
    .middle-row { display:grid; grid-template-columns:1fr 340px; gap:20px; margin-bottom:20px; }
    .card { background:#fff; border:1px solid var(--steam); border-radius:14px; overflow:hidden; }
    .card-header {
      padding:18px 22px 14px; border-bottom:1px solid var(--steam);
      display:flex; align-items:center; justify-content:space-between;
    }
    .card-title { font-family:'Playfair Display',serif; font-size:15px; font-weight:500; }
    .card-action { font-size:12px; color:var(--latte); text-decoration:none; font-weight:500; transition:color .15s; }
    .card-action:hover { color:var(--roast); }
    .card-body  { padding:20px 22px; }

    /* ── Bar Chart ── */
    .chart-area { height:160px; display:flex; align-items:flex-end; gap:6px; }
    .bar-group  { flex:1; display:flex; flex-direction:column; align-items:center; gap:4px; }
    .bar-wrap   { width:100%; height:140px; display:flex; align-items:flex-end; }
    .bar        { width:100%; border-radius:4px 4px 0 0; transition:filter .2s; cursor:pointer; }
    .bar:hover  { filter:brightness(.78); }
    .bar-label  { font-size:10px; color:rgba(44,24,16,.4); }

    /* ── Recent Bills ── */
    .order-item {
      display:flex; align-items:center; gap:12px; padding:11px 0;
      border-bottom:1px solid var(--steam); cursor:pointer; transition:background .12s; text-decoration:none;
    }
    .order-item:last-child { border-bottom:none; }
    .order-item:hover { background:var(--foam); margin:0 -22px; padding:11px 22px; }
    .order-num  { font-size:12px; font-weight:500; color:rgba(44,24,16,.35); width:28px; flex-shrink:0; }
    .order-info { flex:1; min-width:0; }
    .order-code { font-size:13px; font-weight:500; }
    .order-meta { font-size:11px; color:rgba(44,24,16,.4); margin-top:1px; }
    .order-amount { font-size:13px; font-weight:500; margin-right:8px; white-space:nowrap; }
    .status-pill { font-size:10px; font-weight:500; padding:3px 9px; border-radius:20px; flex-shrink:0; }
    .status-pill.finish  { background:rgba(61,122,92,.1);  color:var(--leaf); }
    .status-pill.waiting { background:rgba(201,148,58,.12); color:var(--gold); }
    .status-pill.cancel  { background:rgba(192,75,62,.1);  color:var(--warm-red); }

    /* ── Quick Links ── */
    .divider-label { font-size:11px; color:rgba(44,24,16,.35); letter-spacing:1.5px; text-transform:uppercase; margin-bottom:12px; display:flex; align-items:center; gap:10px; }
    .divider-label::after { content:''; flex:1; height:1px; background:var(--steam); }
    .ql-grid { display:grid; grid-template-columns:1fr 1fr 1fr; gap:12px; }
    .quick-link {
      background:#fff; border:1px solid var(--steam); border-radius:14px;
      padding:18px 20px; display:flex; align-items:center; gap:14px;
      cursor:pointer; transition:all .2s; text-decoration:none;
    }
    .quick-link:hover { transform:translateY(-2px); box-shadow:0 6px 20px rgba(44,24,16,.07); border-color:var(--latte); }
    .ql-icon { width:42px; height:42px; border-radius:12px; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
    .ql-green  .ql-icon { background:rgba(61,122,92,.1); }
    .ql-amber  .ql-icon { background:rgba(201,148,58,.1); }
    .ql-blue   .ql-icon { background:rgba(24,95,165,.08); }
    .ql-rose   .ql-icon { background:rgba(163,45,45,.08); }
    .ql-brown  .ql-icon { background:rgba(200,149,108,.12); }
    .ql-purple .ql-icon { background:rgba(83,74,183,.08); }
    .ql-name { font-size:13.5px; font-weight:500; }
    .ql-desc { font-size:11px; color:rgba(44,24,16,.4); margin-top:2px; }
    .ql-arrow { margin-left:auto; color:rgba(44,24,16,.2); transition:all .15s; flex-shrink:0; }
    .quick-link:hover .ql-arrow { color:var(--latte); transform:translateX(2px); }

    /* ── POS CTA ── */
    .pos-cta {
      background:var(--espresso); border-radius:14px; padding:22px 26px;
      display:flex; align-items:center; gap:16px; cursor:pointer; text-decoration:none;
      transition:background .2s; position:relative; overflow:hidden;
    }
    .pos-cta::before { content:''; position:absolute; right:-30px; top:-30px; width:120px; height:120px; border-radius:50%; background:rgba(200,149,108,.1); }
    .pos-cta:hover { background:var(--mocha); }
    .pos-icon { width:48px; height:48px; border-radius:14px; background:rgba(200,149,108,.15); display:flex; align-items:center; justify-content:center; flex-shrink:0; }
    .pos-title { font-family:'Playfair Display',serif; font-size:17px; color:var(--foam); }
    .pos-desc  { font-size:12px; color:rgba(255,255,255,.45); margin-top:3px; }
    .pos-btn {
      margin-left:auto; background:var(--latte); color:#fff;
      border:none; border-radius:8px; padding:10px 20px; font-size:13px; font-weight:500;
      cursor:pointer; font-family:'DM Sans',sans-serif; transition:background .15s; white-space:nowrap; position:relative; z-index:1;
    }
    .pos-btn:hover { background:#d4a07a; }

    /* ── Legend ── */
    .chart-legend { display:flex; gap:16px; margin-top:10px; }
    .legend-item { display:flex; align-items:center; gap:5px; font-size:11px; color:rgba(44,24,16,.5); }
    .legend-dot  { width:10px; height:10px; border-radius:2px; }
  </style>
</head>
<body>

<%-- ===== SIDEBAR ===== --%>
<div class="sidebar">
  <div class="logo-area">
    <div class="logo-cup">
      <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
        <path d="M3 6h12l-1.5 8H4.5L3 6z" fill="rgba(255,255,255,0.9)"/>
        <path d="M6 6V4a2 2 0 014 0v2" stroke="rgba(255,255,255,0.6)" stroke-width="1.2" fill="none"/>
      </svg>
    </div>
    <div class="logo-title">PolyCoffee</div>
    <div class="logo-sub">Management System</div>
  </div>

  <div class="role-badge">
    <div class="role-dot"></div>
    <c:choose>
      <c:when test="${sessionScope.user.role}">Quản lý</c:when>
      <c:otherwise>Nhân viên</c:otherwise>
    </c:choose>
  </div>

  <nav>
    <div class="nav-section">Tổng quan</div>
    <a class="nav-item active" href="${pageContext.request.contextPath}/manager/dashboard">
      <svg width="15" height="15" viewBox="0 0 15 15" fill="currentColor"><path d="M7.5 1L1 6.5V14h4.5V9.5h4V14H14V6.5L7.5 1z"/></svg>
      Dashboard
    </a>


    <div class="nav-section">Quản lý</div>
    <a class="nav-item" href="${pageContext.request.contextPath}/manager/categories">
      <svg width="15" height="15" viewBox="0 0 15 15" fill="none" stroke="currentColor" stroke-width="1.4"><path d="M2 4h11M4 8h7M6 12h3"/></svg>
      Danh mục
    </a>
    <a class="nav-item" href="${pageContext.request.contextPath}/manager/drinks">
      <svg width="15" height="15" viewBox="0 0 15 15" fill="none" stroke="currentColor" stroke-width="1.4"><path d="M3.5 4h8L10 12H5L3.5 4z"/><path d="M5.5 4V3a2 2 0 014 0v1"/></svg>
      Đồ uống
    </a>
    <a class="nav-item" href="${pageContext.request.contextPath}/manager/staffs">
      <svg width="15" height="15" viewBox="0 0 15 15" fill="none" stroke="currentColor" stroke-width="1.4"><circle cx="7.5" cy="5" r="2.5"/><path d="M3 13c0-2.5 2-4 4.5-4s4.5 1.5 4.5 4"/></svg>
      Nhân viên
    </a>
    <a class="nav-item" href="${pageContext.request.contextPath}/manager/bills">
      <svg width="15" height="15" viewBox="0 0 15 15" fill="none" stroke="currentColor" stroke-width="1.4"><rect x="1.5" y="3" width="12" height="9" rx="1"/><path d="M5 3V1.5M10 3V1.5M1.5 7h12"/></svg>
      Hóa đơn
      <c:if test="${pendingCount > 0}">
        <span class="nav-badge">${pendingCount}</span>
      </c:if>
    </a>
    <a class="nav-item" href="${pageContext.request.contextPath}/manager/statistics">
          <svg width="15" height="15" viewBox="0 0 15 15" fill="none" stroke="currentColor" stroke-width="1.4"><path d="M2 11l3-4 3 2.5 3-5 3 2"/><path d="M2 2v9h12"/></svg>
          Thống kê
    </a>

    <div class="nav-section">Nhân viên</div>
    <a class="nav-item" href="${pageContext.request.contextPath}/employee/pos">
      <svg width="15" height="15" viewBox="0 0 15 15" fill="none" stroke="currentColor" stroke-width="1.4"><rect x="1" y="2" width="13" height="9" rx="1"/><path d="M5 11v2M10 11v2M3 13h9"/><path d="M6 6h3M7.5 5v3"/></svg>
      POS — Bán hàng
    </a>
  </nav>

  <div class="sidebar-footer">
    <div class="user-row">
      <div class="avatar">
        <%-- Show initials from fullName --%>
        <c:set var="fn" value="${sessionScope.user.fullName}"/>
        <c:choose>
          <c:when test="${not empty fn}">${fn.substring(0,1).toUpperCase()}</c:when>
          <c:otherwise>U</c:otherwise>
        </c:choose>
      </div>
      <div style="flex:1;min-width:0;">
        <div class="user-name">${sessionScope.user.fullName}</div>
        <div class="user-role">${sessionScope.user.email}</div>
      </div>
      <a href="${pageContext.request.contextPath}/auth/logout" class="logout-btn" title="Đăng xuất">
        <svg width="15" height="15" viewBox="0 0 15 15" fill="none" stroke="currentColor" stroke-width="1.4">
          <path d="M10 7.5H3M3 7.5L5.5 5M3 7.5L5.5 10"/>
          <path d="M8 4V3a1 1 0 011-1h3a1 1 0 011 1v9a1 1 0 01-1 1h-3a1 1 0 01-1-1v-1"/>
        </svg>
      </a>
    </div>
  </div>
</div>

<%-- ===== MAIN ===== --%>
<main class="main">

  <%-- Topbar --%>
  <div class="topbar">
    <div>
      <div class="page-heading">
        <c:choose>
          <c:when test="${not empty sessionScope.user}">Xin chào, ${sessionScope.user.fullName} ☕</c:when>
          <c:otherwise>Chào buổi sáng ☕</c:otherwise>
        </c:choose>
      </div>
      <div class="page-sub">Tổng quan hoạt động hôm nay của PolyCoffee</div>
    </div>
    <div class="topbar-right">
      <div class="time-chip">
        <svg width="12" height="12" viewBox="0 0 12 12" fill="none" stroke="currentColor" stroke-width="1.3"><circle cx="6" cy="6" r="5"/><path d="M6 3v3l2 1.5"/></svg>
        <span id="clock">--:--</span>
      </div>
      <a href="${pageContext.request.contextPath}/manager/bills" class="notif-btn" title="Đơn đang chờ">
        <svg width="16" height="16" viewBox="0 0 16 16" fill="none" stroke="currentColor" stroke-width="1.4"><path d="M8 1.5a5 5 0 015 5v3l1.5 2.5H1.5L3 9.5v-3a5 5 0 015-5z"/><path d="M6.5 13.5a1.5 1.5 0 003 0"/></svg>
        <c:if test="${pendingCount > 0}"><div class="notif-dot"></div></c:if>
      </a>
    </div>
  </div>

  <%-- Flash message --%>
  <c:if test="${not empty sessionScope.message}">
    <div style="background:rgba(61,122,92,.1);border:1px solid rgba(61,122,92,.25);color:var(--leaf);border-radius:10px;padding:10px 16px;margin-bottom:20px;font-size:13px;">
      ${sessionScope.message}
    </div>
    <c:remove var="message" scope="session"/>
  </c:if>

  <%-- Stat Cards --%>
  <div class="stats-grid">

    <%-- Revenue --%>
    <div class="stat-card revenue" onclick="location.href='${pageContext.request.contextPath}/manager/statistics'">
      <div class="stat-icon">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="#C9943A" stroke-width="1.5"><circle cx="9" cy="9" r="7.5"/><path d="M9 5.5v7M7 11.5c0 .83.67 1.5 2 1.5s2-.67 2-1.5-1-1.5-2-1.5-2-.67-2-1.5S8.17 8 9 8s2 .67 2 1.5"/></svg>
      </div>
      <div class="stat-label">Doanh thu hôm nay</div>
      <div class="stat-value">
        <fmt:formatNumber value="${todayRevenue}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
      </div>
      <div class="stat-change ${todayRevenue >= yesterdayRevenue ? 'up' : 'down'}">
        <c:choose>
          <c:when test="${yesterdayRevenue > 0}">
            <c:set var="revPct" value="${((todayRevenue - yesterdayRevenue) / yesterdayRevenue) * 100}"/>
            <fmt:formatNumber value="${revPct}" maxFractionDigits="1" var="revPctStr"/>
            <c:choose>
              <c:when test="${revPct >= 0}">▲ +${revPctStr}%</c:when>
              <c:otherwise>▼ ${revPctStr}%</c:otherwise>
            </c:choose>
            so với hôm qua
          </c:when>
          <c:otherwise>Chưa có dữ liệu hôm qua</c:otherwise>
        </c:choose>
      </div>
    </div>

    <%-- Bills today --%>
    <div class="stat-card orders" onclick="location.href='${pageContext.request.contextPath}/manager/bills'">
      <div class="stat-icon">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="#C8956C" stroke-width="1.5"><path d="M2.5 4.5h13L14 14H4L2.5 4.5z"/><path d="M6.5 4.5V3a2 2 0 014 0v1.5"/></svg>
      </div>
      <div class="stat-label">Hóa đơn hôm nay</div>
      <div class="stat-value">${todayBillCount}</div>
      <div class="stat-change ${todayBillCount >= yesterdayBillCount ? 'up' : 'down'}">
        <c:set var="billDiff" value="${todayBillCount - yesterdayBillCount}"/>
        <c:choose>
          <c:when test="${billDiff >= 0}">▲ +${billDiff}</c:when>
          <c:otherwise>▼ ${billDiff}</c:otherwise>
        </c:choose>
        so với hôm qua
      </div>
    </div>

    <%-- Active staff --%>
    <div class="stat-card staff" onclick="location.href='${pageContext.request.contextPath}/manager/staffs'">
      <div class="stat-icon">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="#3D7A5C" stroke-width="1.5"><circle cx="9" cy="6" r="3"/><path d="M3.5 16c0-3 2.5-5 5.5-5s5.5 2 5.5 5"/></svg>
      </div>
      <div class="stat-label">Nhân viên hoạt động</div>
      <div class="stat-value">${activeStaff}</div>
      <div class="stat-change neutral">Tổng ${totalStaff} nhân viên</div>
    </div>

    <%-- Pending --%>
    <div class="stat-card pending" onclick="location.href='${pageContext.request.contextPath}/manager/bills'">
      <div class="stat-icon">
        <svg width="18" height="18" viewBox="0 0 18 18" fill="none" stroke="#C04B3E" stroke-width="1.5"><circle cx="9" cy="9" r="7.5"/><path d="M9 5.5v4l2.5 2.5"/></svg>
      </div>
      <div class="stat-label">Đơn đang chờ</div>
      <div class="stat-value">${pendingCount}</div>
      <div class="stat-change ${pendingCount > 0 ? 'down' : 'neutral'}">
        <c:choose>
          <c:when test="${pendingCount > 0}">Cần xử lý ngay</c:when>
          <c:otherwise>Không có đơn chờ</c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

  <%-- Middle Row --%>
  <div class="middle-row">

    <%-- Revenue Chart --%>
    <div class="card">
      <div class="card-header">
        <div class="card-title">Doanh thu 7 ngày qua</div>
        <a class="card-action" href="${pageContext.request.contextPath}/manager/statistics">Xem chi tiết →</a>
      </div>
      <div class="card-body">
        <%-- Pass Java array to JS as JSON --%>
        <script>
          var chartRevenue = [<c:forEach var="v" items="${chartRevenue}" varStatus="s">${v}<c:if test="${!s.last}">,</c:if></c:forEach>];
          var chartLabels  = [<c:forEach var="l" items="${chartLabels}"  varStatus="s">"${l}"<c:if test="${!s.last}">,</c:if></c:forEach>];
        </script>
        <div class="chart-area" id="revenueChart"></div>
        <div class="chart-legend">
          <div class="legend-item"><div class="legend-dot" style="background:var(--espresso)"></div>Doanh thu (nghìn đồng)</div>
        </div>
      </div>
    </div>

    <%-- Recent Bills --%>
    <div class="card">
      <div class="card-header">
        <div class="card-title">Hóa đơn gần đây</div>
        <a class="card-action" href="${pageContext.request.contextPath}/manager/bills">Tất cả →</a>
      </div>
      <div class="card-body" style="padding-top:4px;padding-bottom:4px;">
        <c:forEach var="b" items="${recentBills}" varStatus="s">
          <a class="order-item" href="${pageContext.request.contextPath}/manager/bills?action=detail&id=${b.id}">
            <div class="order-num">#${b.id}</div>
            <div class="order-info">
              <div class="order-code">${b.code}</div>
              <div class="order-meta">
                ${staffNames[b.userId]}
                · <fmt:formatDate value="${b.createdAt}" pattern="HH:mm"/>
              </div>
            </div>
            <div class="order-amount">
              <fmt:formatNumber value="${b.total}" type="number" groupingUsed="true" maxFractionDigits="0"/>đ
            </div>
            <div class="status-pill ${b.status}">
              <c:choose>
                <c:when test="${b.status eq 'finish'}">Xong</c:when>
                <c:when test="${b.status eq 'waiting'}">Chờ</c:when>
                <c:otherwise>Huỷ</c:otherwise>
              </c:choose>
            </div>
          </a>
        </c:forEach>
        <c:if test="${empty recentBills}">
          <div style="font-size:13px;color:rgba(44,24,16,.4);padding:16px 0;text-align:center;">Chưa có hóa đơn nào hôm nay</div>
        </c:if>
      </div>
    </div>
  </div>

  <%-- Quick Links --%>
  <div class="divider-label">Truy cập nhanh</div>
  <div style="margin-bottom:12px;">
    <a class="pos-cta" href="${pageContext.request.contextPath}/employee/pos">
      <div class="pos-icon">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#C8956C" stroke-width="1.6"><rect x="2" y="4" width="20" height="14" rx="2"/><path d="M8 18v2M16 18v2M5 18h14"/><path d="M9 9h6M12 7v4"/></svg>
      </div>
      <div>
        <div class="pos-title">Mở màn hình POS — Bắt đầu bán hàng</div>
        <div class="pos-desc">Tạo đơn mới, thêm đồ uống, thanh toán nhanh chóng</div>
      </div>
      <button class="pos-btn">Mở POS →</button>
    </a>
  </div>

  <div class="ql-grid">
    <a class="quick-link ql-green" href="${pageContext.request.contextPath}/manager/categories">
      <div class="ql-icon"><svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#3D7A5C" stroke-width="1.5"><path d="M3 6h14M5 10h9M7 14h6"/></svg></div>
      <div><div class="ql-name">Danh mục</div><div class="ql-desc">Phân loại đồ uống</div></div>
      <div class="ql-arrow">→</div>
    </a>
    <a class="quick-link ql-amber" href="${pageContext.request.contextPath}/manager/drinks">
      <div class="ql-icon"><svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#C9943A" stroke-width="1.5"><path d="M4.5 5h11L14 16H6L4.5 5z"/><path d="M7 5V4a3 3 0 016 0v1"/></svg></div>
      <div><div class="ql-name">Đồ uống</div><div class="ql-desc">Thêm, sửa, xoá sản phẩm</div></div>
      <div class="ql-arrow">→</div>
    </a>
    <a class="quick-link ql-blue" href="${pageContext.request.contextPath}/manager/staffs">
      <div class="ql-icon"><svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#185FA5" stroke-width="1.5"><circle cx="10" cy="6" r="3"/><path d="M3.5 18c0-3.5 3-5.5 6.5-5.5s6.5 2 6.5 5.5"/></svg></div>
      <div><div class="ql-name">Nhân viên</div><div class="ql-desc">Cấp phép, reset mật khẩu</div></div>
      <div class="ql-arrow">→</div>
    </a>
    <a class="quick-link ql-rose" href="${pageContext.request.contextPath}/manager/bills">
      <div class="ql-icon"><svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#A32D2D" stroke-width="1.5"><path d="M5 2h10a2 2 0 012 2v14l-3-2-2 2-2-2-2 2-3-2V4a2 2 0 012-2z"/><path d="M8 8h4M8 12h6"/></svg></div>
      <div><div class="ql-name">Hóa đơn</div><div class="ql-desc">Xem &amp; duyệt đơn hàng</div></div>
      <div class="ql-arrow">→</div>
    </a>
    <a class="quick-link ql-purple" href="${pageContext.request.contextPath}/manager/statistics">
      <div class="ql-icon"><svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#534AB7" stroke-width="1.5"><path d="M3 15l4-5 4 3 4-7 3 2"/><path d="M3 3v12h14"/></svg></div>
      <div><div class="ql-name">Thống kê</div><div class="ql-desc">Biểu đồ doanh thu</div></div>
      <div class="ql-arrow">→</div>
    </a>
    <a class="quick-link ql-brown" href="${pageContext.request.contextPath}/manager/statistics?action=top-drinks">
      <div class="ql-icon"><svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="#7B4A2D" stroke-width="1.5"><path d="M5 18V10M9 18V5M13 18V11M17 18V8"/></svg></div>
      <div><div class="ql-name">Best Sellers</div><div class="ql-desc">Top 5 bán chạy nhất</div></div>
      <div class="ql-arrow">→</div>
    </a>
  </div>

</main>

<script>
  // Live clock
  function tick() {
    var now = new Date();
    document.getElementById('clock').textContent =
      String(now.getHours()).padStart(2,'0') + ':' + String(now.getMinutes()).padStart(2,'0');
  }
  tick(); setInterval(tick, 30000);

  // Revenue bar chart (from Java array)
  (function() {
    var container = document.getElementById('revenueChart');
    if (!container || !chartRevenue) return;
    var max = Math.max.apply(null, chartRevenue) || 1;
    container.style.display = 'flex';
    container.style.alignItems = 'flex-end';
    container.style.gap = '6px';
    chartRevenue.forEach(function(val, i) {
      var group = document.createElement('div');
      group.style.cssText = 'flex:1;display:flex;flex-direction:column;align-items:center;gap:4px;';

      var wrap = document.createElement('div');
      wrap.style.cssText = 'width:100%;height:140px;display:flex;align-items:flex-end;';

      var bar = document.createElement('div');
      var h = Math.max(4, Math.round((val / max) * 140));
      bar.style.cssText = 'width:100%;height:' + h + 'px;border-radius:4px 4px 0 0;background:#2C1810;cursor:pointer;transition:filter .2s;';
      bar.title = chartLabels[i] + ': ' + val.toLocaleString() + 'đ';
      bar.onmouseenter = function(){ this.style.filter='brightness(.75)'; };
      bar.onmouseleave = function(){ this.style.filter=''; };

      var label = document.createElement('div');
      label.style.cssText = 'font-size:10px;color:rgba(44,24,16,.4);font-family:DM Sans,sans-serif;';
      label.textContent = chartLabels[i];

      wrap.appendChild(bar);
      group.appendChild(wrap);
      group.appendChild(label);
      container.appendChild(group);
    });
  })();
</script>

</body>
</html>
