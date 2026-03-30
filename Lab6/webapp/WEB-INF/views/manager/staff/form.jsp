<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>${staff.id > 0 ? 'Sửa' : 'Thêm'} nhân viên — PolyCoffee</title>
  <c:set var="activeNav" value="staffs" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <a href="${pageContext.request.contextPath}/manager/staffs" class="pc-back">
        <svg width="14" height="14" viewBox="0 0 14 14" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M9 11L5 7l4-4"/></svg>
        Quay lại nhân viên
      </a>
      <div class="pc-page-title" style="margin-top:8px;">${staff.id > 0 ? 'Chỉnh sửa nhân viên' : 'Thêm nhân viên mới'}</div>
    </div>
  </div>

  <div class="pc-card" style="max-width:520px;">
    <div class="pc-card-hd">
      <div class="pc-card-title">${staff.id > 0 ? 'Cập nhật thông tin' : 'Thông tin nhân viên'}</div>
    </div>
    <div class="pc-card-bd">
      <form method="post" action="${pageContext.request.contextPath}/manager/staffs">
        <input type="hidden" name="action" value="${staff.id > 0 ? 'update' : 'create'}">
        <c:if test="${staff.id > 0}">
          <input type="hidden" name="id" value="${staff.id}">
        </c:if>

        <div class="pc-form-group">
          <label class="pc-label">Họ và tên</label>
          <input type="text" name="fullName" class="pc-input ${not empty errors.fullName ? 'is-invalid' : ''}"
                 value="${staff.fullName}" placeholder="Nguyễn Văn A">
          <c:if test="${not empty errors.fullName}"><div class="pc-invalid-msg">${errors.fullName}</div></c:if>
        </div>

        <div class="pc-form-group">
          <label class="pc-label">Email</label>
          <input type="email" name="email" class="pc-input ${not empty errors.email ? 'is-invalid' : ''}"
                 value="${staff.email}" placeholder="staff@polycoffee.com">
          <c:if test="${not empty errors.email}"><div class="pc-invalid-msg">${errors.email}</div></c:if>
        </div>

        <div class="pc-form-group">
          <label class="pc-label">
            Mật khẩu
            <c:if test="${staff.id > 0}"><span style="font-weight:400;color:rgba(44,24,16,.4);"> — để trống nếu không đổi</span></c:if>
          </label>
          <input type="password" name="password" class="pc-input ${not empty errors.password ? 'is-invalid' : ''}" placeholder="••••••">
          <c:if test="${not empty errors.password}"><div class="pc-invalid-msg">${errors.password}</div></c:if>
        </div>

        <div class="pc-form-group">
          <label class="pc-label">Số điện thoại</label>
          <input type="text" name="phone" class="pc-input" value="${staff.phone}" placeholder="0901234567">
        </div>

        <div class="pc-form-group">
          <label class="pc-check">
            <input type="checkbox" name="active" value="true" ${staff.active || empty staff ? 'checked' : ''}>
            Tài khoản đang hoạt động
          </label>
        </div>

        <div style="display:flex;gap:10px;margin-top:8px;">
          <button type="submit" class="pc-btn primary">Lưu thay đổi</button>
          <a href="${pageContext.request.contextPath}/manager/staffs" class="pc-btn ghost">Hủy</a>
        </div>
      </form>
    </div>
  </div>
</main>
</body>
</html>
