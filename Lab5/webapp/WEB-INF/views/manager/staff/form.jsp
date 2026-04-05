<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${staff.id > 0 ? 'Sửa' : 'Thêm'} nhân viên</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand navbar-dark bg-dark">
    <div class="container">
        <a class="navbar-brand" href="#">PolyCoffee</a>
        <div class="navbar-nav">
            <a class="nav-link" href="${pageContext.request.contextPath}/manager/categories">Loại đồ uống</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/manager/drinks">Đồ uống</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/manager/staffs">Nhân viên</a>
        </div>
        <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline-light btn-sm">Đăng xuất</a>
    </div>
</nav>
<div class="container mt-4">
    <h4>${staff.id > 0 ? 'Sửa' : 'Thêm'} nhân viên</h4>

    <form method="post" action="${pageContext.request.contextPath}/manager/staffs">
        <input type="hidden" name="_csrf" value="${sessionScope._csrf}">
        <input type="hidden" name="action" value="${staff.id > 0 ? 'update' : 'create'}">
        <c:if test="${staff.id > 0}">
            <input type="hidden" name="id" value="${staff.id}">
        </c:if>

        <div class="mb-3">
            <label class="form-label">Họ tên</label>
            <input type="text" name="fullName" class="form-control ${not empty errors.fullName ? 'is-invalid' : ''}"
                   value="${staff.fullName}">
            <c:if test="${not empty errors.fullName}">
                <div class="invalid-feedback">${errors.fullName}</div>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Email</label>
            <input type="email" name="email" class="form-control ${not empty errors.email ? 'is-invalid' : ''}"
                   value="${staff.email}">
            <c:if test="${not empty errors.email}">
                <div class="invalid-feedback">${errors.email}</div>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Mật khẩu ${staff.id > 0 ? '(để trống nếu không đổi)' : ''}</label>
            <input type="password" name="password" class="form-control ${not empty errors.password ? 'is-invalid' : ''}">
            <c:if test="${not empty errors.password}">
                <div class="invalid-feedback">${errors.password}</div>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Số điện thoại</label>
            <input type="text" name="phone" class="form-control" value="${staff.phone}">
        </div>

        <div class="mb-3 form-check">
            <input type="checkbox" name="active" value="true" class="form-check-input"
                   ${staff.active || empty staff ? 'checked' : ''}>
            <label class="form-check-label">Hoạt động</label>
        </div>

        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="${pageContext.request.contextPath}/manager/staffs" class="btn btn-secondary">Hủy</a>
    </form>
</div>
</body>
</html>
