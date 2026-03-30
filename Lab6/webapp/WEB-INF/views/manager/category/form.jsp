<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>${category.id > 0 ? 'Sửa' : 'Thêm'} danh mục — PolyCoffee</title>
  <c:set var="activeNav" value="categories" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <a href="${pageContext.request.contextPath}/manager/categories" class="pc-back">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 11L5 7l4-4"/></svg>
        Quay lại danh mục
      </a>
      <div class="pc-page-title" style="margin-top:8px;">${category.id > 0 ? 'Chỉnh sửa danh mục' : 'Thêm danh mục mới'}</div>
    </div>
  </div>

  <div class="pc-card" style="max-width:520px;">
    <div class="pc-card-hd">
      <div class="pc-card-title">${category.id > 0 ? 'Cập nhật thông tin' : 'Thông tin danh mục'}</div>
    </div>
    <div class="pc-card-bd">
      <form method="post" action="${pageContext.request.contextPath}/manager/categories">
        <input type="hidden" name="action" value="${category.id > 0 ? 'update' : 'create'}">
        <c:if test="${category.id > 0}">
          <input type="hidden" name="id" value="${category.id}">
        </c:if>

        <div class="pc-form-group">
          <label class="pc-label">Tên danh mục</label>
          <input type="text" name="name" class="pc-input ${not empty errors.name ? 'is-invalid' : ''}"
                 value="${category.name}" placeholder="Ví dụ: Cà phê, Trà sữa...">
          <c:if test="${not empty errors.name}">
            <div class="pc-invalid-msg">${errors.name}</div>
          </c:if>
        </div>

        <div class="pc-form-group">
          <label class="pc-check">
            <input type="checkbox" name="active" value="true" ${category.active || empty category ? 'checked' : ''}>
            Danh mục đang hoạt động
          </label>
        </div>

        <div style="display:flex;gap:10px;margin-top:8px;">
          <button type="submit" class="pc-btn primary">Lưu thay đổi</button>
          <a href="${pageContext.request.contextPath}/manager/categories" class="pc-btn ghost">Hủy</a>
        </div>
      </form>
    </div>
  </div>
</main>
</body>
</html>
