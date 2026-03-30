<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>${drink.id > 0 ? 'Sửa' : 'Thêm'} đồ uống — PolyCoffee</title>
  <c:set var="activeNav" value="drinks" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <a href="${pageContext.request.contextPath}/manager/drinks" class="pc-back">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 11L5 7l4-4"/></svg>
        Quay lại đồ uống
      </a>
      <div class="pc-page-title" style="margin-top:8px;">${drink.id > 0 ? 'Chỉnh sửa đồ uống' : 'Thêm đồ uống mới'}</div>
    </div>
  </div>

  <div class="pc-card" style="max-width:580px;">
    <div class="pc-card-hd">
      <div class="pc-card-title">${drink.id > 0 ? 'Cập nhật thông tin' : 'Thông tin đồ uống'}</div>
    </div>
    <div class="pc-card-bd">
      <form method="post" action="${pageContext.request.contextPath}/manager/drinks" enctype="multipart/form-data">
        <input type="hidden" name="action" value="${drink.id > 0 ? 'update' : 'create'}">
        <c:if test="${drink.id > 0}">
          <input type="hidden" name="id" value="${drink.id}">
          <input type="hidden" name="oldImage" value="${drink.image}">
        </c:if>

        <div class="pc-form-group">
          <label class="pc-label">Danh mục</label>
          <select name="categoryId" class="pc-select ${not empty errors.categoryId ? 'is-invalid' : ''}">
            <option value="0">— Chọn danh mục —</option>
            <c:forEach var="cat" items="${categories}">
              <option value="${cat.id}" ${cat.id == drink.categoryId ? 'selected' : ''}>${cat.name}</option>
            </c:forEach>
          </select>
          <c:if test="${not empty errors.categoryId}"><div class="pc-invalid-msg">${errors.categoryId}</div></c:if>
        </div>

        <div class="pc-form-group">
          <label class="pc-label">Tên đồ uống</label>
          <input type="text" name="name" class="pc-input ${not empty errors.name ? 'is-invalid' : ''}" value="${drink.name}" placeholder="Ví dụ: Cà phê sữa đá">
          <c:if test="${not empty errors.name}"><div class="pc-invalid-msg">${errors.name}</div></c:if>
        </div>

        <div class="pc-form-group">
          <label class="pc-label">Giá (VNĐ)</label>
          <input type="number" name="price" class="pc-input ${not empty errors.price ? 'is-invalid' : ''}"
                 value="${drink.price > 0 ? drink.price : ''}" min="0" placeholder="Ví dụ: 35000">
          <c:if test="${not empty errors.price}"><div class="pc-invalid-msg">${errors.price}</div></c:if>
        </div>

        <div class="pc-form-group">
          <label class="pc-label">Hình ảnh</label>
          <input type="file" name="imageFile" class="pc-input" accept="image/*" style="padding:7px 13px;">
          <c:if test="${not empty drink.image}">
            <div style="margin-top:8px;display:flex;align-items:center;gap:10px;">
              <img src="${pageContext.request.contextPath}/uploads/${drink.image}"
                   style="width:56px;height:56px;object-fit:cover;border-radius:8px;border:1px solid var(--steam);">
              <span style="font-size:12px;color:rgba(44,24,16,.45);">Ảnh hiện tại: ${drink.image}</span>
            </div>
          </c:if>
        </div>

        <div class="pc-form-group">
          <label class="pc-label">Mô tả</label>
          <textarea name="description" class="pc-textarea" rows="3" placeholder="Mô tả ngắn về đồ uống...">${drink.description}</textarea>
        </div>

        <div class="pc-form-group">
          <label class="pc-check">
            <input type="checkbox" name="active" value="true" ${drink.active || empty drink ? 'checked' : ''}>
            Đồ uống đang hoạt động (hiển thị trên POS)
          </label>
        </div>

        <div style="display:flex;gap:10px;margin-top:8px;">
          <button type="submit" class="pc-btn primary">Lưu thay đổi</button>
          <a href="${pageContext.request.contextPath}/manager/drinks" class="pc-btn ghost">Hủy</a>
        </div>
      </form>
    </div>
  </div>
</main>
</body>
</html>
