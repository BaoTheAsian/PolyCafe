<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <title>Danh mục — PolyCoffee</title>
  <c:set var="activeNav" value="categories" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <div class="pc-page-title">Danh mục đồ uống</div>
      <div class="pc-page-sub">Quản lý phân loại sản phẩm</div>
    </div>
    <a href="${pageContext.request.contextPath}/manager/categories?action=create" class="pc-btn primary">
      <svg width="13" height="13" viewBox="0 0 13 13" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M6.5 1v11M1 6.5h11"/></svg>
      Thêm mới
    </a>
  </div>

  <c:if test="${not empty sessionScope.message}">
    <div class="pc-alert success">${sessionScope.message}</div>
    <c:remove var="message" scope="session"/>
  </c:if>
  <c:if test="${not empty sessionScope.error}">
    <div class="pc-alert danger">${sessionScope.error}</div>
    <c:remove var="error" scope="session"/>
  </c:if>

  <div class="pc-card">
    <div class="pc-card-hd">
      <div class="pc-card-title">Tất cả danh mục</div>
      <span style="font-size:12px;color:rgba(44,24,16,.4);">${categories.size()} danh mục</span>
    </div>
    <div style="padding:0;">
      <table class="pc-table">
        <thead>
          <tr>
            <th style="width:60px;">ID</th>
            <th>Tên danh mục</th>
            <th style="width:130px;">Trạng thái</th>
            <th style="width:160px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="cat" items="${categories}">
            <tr>
              <td style="color:rgba(44,24,16,.4);font-size:12px;">#${cat.id}</td>
              <td style="font-weight:500;">${cat.name}</td>
              <td>
                <span class="pc-pill ${cat.active ? 'active' : 'off'}">
                  ${cat.active ? 'Hoạt động' : 'Không hoạt động'}
                </span>
              </td>
              <td>
                <div style="display:flex;gap:6px;">
                  <a href="${pageContext.request.contextPath}/manager/categories?action=edit&id=${cat.id}" class="pc-btn ghost sm">Sửa</a>
                  <a href="${pageContext.request.contextPath}/manager/categories?action=delete&id=${cat.id}"
                     class="pc-btn danger sm"
                     onclick="return confirm('Xóa danh mục «${cat.name}»?')">Xóa</a>
                </div>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty categories}">
            <tr><td colspan="4" style="text-align:center;padding:32px;color:rgba(44,24,16,.4);">Chưa có danh mục nào</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</main>
</body>
</html>
