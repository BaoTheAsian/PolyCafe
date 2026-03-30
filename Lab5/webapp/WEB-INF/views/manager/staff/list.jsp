<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Quản lý nhân viên</title>
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
    <h4>Quản lý nhân viên</h4>

    <c:if test="${not empty sessionScope.message}">
        <div class="alert alert-success">${sessionScope.message}</div>
        <c:remove var="message" scope="session"/>
    </c:if>

    <!-- Bộ lọc tìm kiếm -->
    <form method="get" action="${pageContext.request.contextPath}/manager/staffs" class="row g-2 mb-3">
        <div class="col-md-3">
            <input type="text" name="keyword" class="form-control" placeholder="Tên nhân viên" value="${keyword}">
        </div>
        <div class="col-md-3">
            <input type="text" name="email" class="form-control" placeholder="Email" value="${email}">
        </div>
        <div class="col-md-2">
            <select name="active" class="form-select">
                <option value="-1" ${active == -1 ? 'selected' : ''}>-- Trạng thái --</option>
                <option value="1" ${active == 1 ? 'selected' : ''}>Hoạt động</option>
                <option value="0" ${active == 0 ? 'selected' : ''}>Khóa</option>
            </select>
        </div>
        <div class="col-md-2">
            <button type="submit" class="btn btn-primary">Tìm kiếm</button>
        </div>
        <div class="col-md-2 text-end">
            <a href="?action=create" class="btn btn-success">+ Thêm mới</a>
        </div>
    </form>

    <table class="table table-bordered table-striped">
        <thead class="table-dark">
        <tr>
            <th>ID</th><th>Họ tên</th><th>Email</th><th>SĐT</th><th>Trạng thái</th><th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="s" items="${staffs}">
            <tr>
                <td>${s.id}</td>
                <td>${s.fullName}</td>
                <td>${s.email}</td>
                <td>${s.phone}</td>
                <td><span class="badge ${s.active ? 'bg-success' : 'bg-danger'}">${s.active ? 'HĐ' : 'Khóa'}</span></td>
                <td>
                    <a href="?action=edit&id=${s.id}" class="btn btn-warning btn-sm">Sửa</a>
                    <a href="?action=toggle-active&id=${s.id}" class="btn btn-secondary btn-sm">${s.active ? 'Khóa' : 'Mở'}</a>
                    <a href="?action=reset-password&id=${s.id}" class="btn btn-info btn-sm"
                       onclick="return confirm('Cấp lại mật khẩu?')">Reset MK</a>
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
                        <a class="page-link" href="?keyword=${keyword}&email=${email}&active=${active}&page=${i}">${i}</a>
                    </li>
                </c:forEach>
            </ul>
        </nav>
    </c:if>
</div>
</body>
</html>
