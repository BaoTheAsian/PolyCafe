<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quản lý đồ uống</title>
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
    <h4>Quản lý đồ uống</h4>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success">${sessionScope.message}</div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <!-- Bộ lọc tìm kiếm -->
    <form method="get" action="${pageContext.request.contextPath}/manager/drinks" class="row g-2 mb-3">
        <div class="col-md-3">
            <input type="text" name="keyword" class="form-control" placeholder="Tên đồ uống" value="${keyword}">
        </div>
        <div class="col-md-3">
            <select name="categoryId" class="form-select">
                <option value="0">-- Tất cả loại --</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.id}" ${cat.id == categoryId ? 'selected' : ''}>${cat.name}</option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <select name="active" class="form-select">
                <option value="-1" ${active == -1 ? 'selected' : ''}>-- Trạng thái --</option>
                <option value="1" ${active == 1 ? 'selected' : ''}>Hoạt động</option>
                <option value="0" ${active == 0 ? 'selected' : ''}>Không hoạt động</option>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
        </div>
        <div class="col-md-2 text-end">
            <a href="${pageContext.request.contextPath}/manager/drinks?action=create" class="btn btn-success">+ Thêm mới</a>
        </div>
    </form>

    <table class="table table-bordered table-striped">
        <thead class="table-dark">
        <tr>
            <th>ID</th><th>Hình ảnh</th><th>Tên</th><th>Giá</th><th>Loại</th><th>Trạng thái</th><th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="d" items="${drinks}">
            <tr>
                <td>${d.id}</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty d.image}">
                            <img src="${pageContext.request.contextPath}/uploads/${d.image}"
                                 style="width:60px;height:60px;object-fit:cover;border-radius:4px" alt="${d.name}">
                        </c:when>
                        <c:otherwise><span class="text-muted">No img</span></c:otherwise>
                    </c:choose>
                </td>
                <td>${d.name}</td>
                <td><fmt:formatNumber value="${d.price}" pattern="#,###"/> đ</td>
                <td>${d.categoryId}</td>
                <td><span class="badge ${d.active ? 'bg-success' : 'bg-secondary'}">${d.active ? 'HĐ' : 'Tắt'}</span></td>
                <td>
                    <a href="?action=edit&id=${d.id}" class="btn btn-warning btn-sm">Sửa</a>
                    <a href="?action=delete&id=${d.id}" class="btn btn-danger btn-sm" onclick="return confirm('Xóa?')">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <!-- Phân trang -->
    <c:if test="${totalPages > 1}">
        <nav>
            <ul class="pagination">
                <c:forEach begin="1" end="${totalPages}" var="i">
                    <li class="page-item ${i == currentPage ? 'active' : ''}">
                        <a class="page-link" href="?keyword=${keyword}&categoryId=${categoryId}&active=${active}&page=${i}">${i}</a>
                    </li>
                </c:forEach>
            </ul>
        </nav>
    </c:if>
</div>
</body>
</html>
