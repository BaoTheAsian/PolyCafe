<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>${drink.id > 0 ? 'Sửa' : 'Thêm'} đồ uống</title>
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
    <h4>${drink.id > 0 ? 'Sửa' : 'Thêm'} đồ uống</h4>

    <form method="post" action="${pageContext.request.contextPath}/manager/drinks" enctype="multipart/form-data">
        <input type="hidden" name="_csrf" value="${sessionScope._csrf}">
        <input type="hidden" name="action" value="${drink.id > 0 ? 'update' : 'create'}">
        <c:if test="${drink.id > 0}">
            <input type="hidden" name="id" value="${drink.id}">
            <input type="hidden" name="oldImage" value="${drink.image}">
        </c:if>

        <div class="mb-3">
            <label class="form-label">Loại đồ uống</label>
            <select name="categoryId" class="form-select ${not empty errors.categoryId ? 'is-invalid' : ''}">
                <option value="0">-- Chọn loại --</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.id}" ${cat.id == drink.categoryId ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
            </select>
            <c:if test="${not empty errors.categoryId}">
                <div class="invalid-feedback">${errors.categoryId}</div>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Tên đồ uống</label>
            <input type="text" name="name" class="form-control ${not empty errors.name ? 'is-invalid' : ''}"
                   value="${drink.name}">
            <c:if test="${not empty errors.name}">
                <div class="invalid-feedback">${errors.name}</div>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Giá</label>
            <input type="number" name="price" class="form-control ${not empty errors.price ? 'is-invalid' : ''}"
                   value="${drink.price > 0 ? drink.price : ''}" min="0">
            <c:if test="${not empty errors.price}">
                <div class="invalid-feedback">${errors.price}</div>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Hình ảnh</label>
            <input type="file" name="imageFile" class="form-control" accept="image/*">
            <c:if test="${not empty drink.image}">
                <small class="text-muted">Ảnh hiện tại: ${drink.image}</small>
            </c:if>
        </div>

        <div class="mb-3">
            <label class="form-label">Mô tả</label>
            <textarea name="description" class="form-control" rows="3">${drink.description}</textarea>
        </div>

        <div class="mb-3 form-check">
            <input type="checkbox" name="active" value="true" class="form-check-input"
                   ${drink.active || empty drink ? 'checked' : ''}>
            <label class="form-check-label">Hoạt động</label>
        </div>

        <button type="submit" class="btn btn-primary">Lưu</button>
        <a href="${pageContext.request.contextPath}/manager/drinks" class="btn btn-secondary">Hủy</a>
    </form>
</div>
</body>
</html>
