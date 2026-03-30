<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${empty category.name ? 'Thêm' : 'Sửa'} loại đồ uống</title>
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
    <h4>${category.id > 0 ? 'Sửa' : 'Thêm'} loại đồ uống</h4>

    <form method="post" action="${pageContext.request.contextPath}/manager/categories">
        <input type="hidden" name="action" value="${category.id > 0 ? 'update' : 'create'}">
        <c:if test="${category.id > 0}">
            <input type="hidden" name="id" value="${category.id}">
        </c:if>

        <div class="mb-3">
            <label class="form-label">Tên loại đồ uống</label>
            <input type="text" name="name" class="form-control ${not empty errors.name ? 'is-invalid' : ''}"
                   value="${category.name}">
            <c:if test="${not empty errors.name}">
                <div class="invalid-feedback">${errors.name}</div>
            </c:if>
        </div>

        <div class="mb-3 form-check">
            <input type="checkbox" name="active" value="true" class="form-check-input"
                   ${category.active || empty category ? 'checked' : ''}>
            <label class="form-check-label">Hoạt động</label>
        </div>

        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="${pageContext.request.contextPath}/manager/categories" class="btn btn-secondary">Hủy</a>
    </form>
</div>
</body>
</html>
