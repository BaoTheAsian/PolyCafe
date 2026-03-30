<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Nhân viên — PolyCoffee</title>
  <c:set var="activeNav" value="staffs" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <div class="pc-page-title">Quản lý nhân viên</div>
      <div class="pc-page-sub">Cấp tài khoản, phân quyền, reset mật khẩu</div>
    </div>
    <a href="?action=create" class="pc-btn primary">
      <svg width="13" height="13" viewBox="0 0 13 13" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M6.5 1v11M1 6.5h11"/></svg>
      Thêm nhân viên
    </a>
  </div>

  <c:if test="${not empty sessionScope.message}">
    <div class="pc-alert success">${sessionScope.message}</div>
    <c:remove var="message" scope="session"/>
  </c:if>

  <form method="get" action="${pageContext.request.contextPath}/manager/staffs" class="pc-search-row">
    <input type="text" name="keyword" class="pc-input" placeholder="Tên nhân viên..." value="${keyword}" style="max-width:200px;">
    <input type="text" name="email"   class="pc-input" placeholder="Email..."         value="${email}"   style="max-width:200px;">
    <select name="active" class="pc-select" style="max-width:160px;">
      <option value="-1" ${active == -1 ? 'selected' : ''}>Tất cả trạng thái</option>
      <option value="1"  ${active == 1  ? 'selected' : ''}>Hoạt động</option>
      <option value="0"  ${active == 0  ? 'selected' : ''}>Đã khóa</option>
    </select>
    <button type="submit" class="pc-btn latte">Tìm kiếm</button>
  </form>

  <div class="pc-card">
    <div style="padding:0;">
      <table class="pc-table">
        <thead>
          <tr>
            <th style="width:56px;">ID</th>
            <th>Họ tên</th>
            <th>Email</th>
            <th style="width:130px;">SĐT</th>
            <th style="width:120px;">Trạng thái</th>
            <th style="width:230px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="s" items="${staffs}">
            <tr>
              <td style="color:rgba(44,24,16,.4);font-size:12px;">#${s.id}</td>
              <td>
                <div style="display:flex;align-items:center;gap:9px;">
                  <div style="width:30px;height:30px;border-radius:50%;background:var(--foam);border:1px solid var(--steam);display:flex;align-items:center;justify-content:center;font-size:11px;font-weight:500;color:var(--roast);flex-shrink:0;">
                    ${s.fullName.substring(0,1).toUpperCase()}
                  </div>
                  <span style="font-weight:500;">${s.fullName}</span>
                </div>
              </td>
              <td style="color:rgba(44,24,16,.6);">${s.email}</td>
              <td style="color:rgba(44,24,16,.6);">${s.phone}</td>
              <td><span class="pc-pill ${s.active ? 'active' : 'locked'}">${s.active ? 'Hoạt động' : 'Đã khóa'}</span></td>
              <td>
                <div style="display:flex;gap:5px;flex-wrap:wrap;">
                  <a href="?action=edit&id=${s.id}" class="pc-btn ghost sm">Sửa</a>
                  <a href="?action=toggle-active&id=${s.id}" class="pc-btn ghost sm">${s.active ? 'Khóa' : 'Mở khóa'}</a>
                  <a href="?action=reset-password&id=${s.id}" class="pc-btn ghost sm"
                     onclick="return confirm('Cấp lại mật khẩu cho ${s.fullName}?')">Reset MK</a>
                </div>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty staffs}">
            <tr><td colspan="6" style="text-align:center;padding:32px;color:rgba(44,24,16,.4);">Không tìm thấy nhân viên nào</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>

  <c:if test="${totalPages > 1}">
    <div class="pc-pagination">
      <c:forEach begin="1" end="${totalPages}" var="i">
        <a href="?keyword=${keyword}&email=${email}&active=${active}&page=${i}"
           class="pc-page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
      </c:forEach>
    </div>
  </c:if>
</main>
</body>
</html>
