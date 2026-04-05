<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <title>Đồ uống — PolyCoffee</title>
  <c:set var="activeNav" value="drinks" scope="request"/>
</head>
<body>
<%@ include file="/WEB-INF/views/common/sidebar.jspf" %>
<main class="pc-main">
  <div class="pc-topbar">
    <div>
      <div class="pc-page-title">Quản lý đồ uống</div>
      <div class="pc-page-sub">Thêm, sửa, ẩn/hiện sản phẩm</div>
    </div>
    <a href="${pageContext.request.contextPath}/manager/drinks?action=create" class="pc-btn primary">
      <svg width="13" height="13" viewBox="0 0 13 13" fill="none" stroke="currentColor" stroke-width="1.8"><path d="M6.5 1v11M1 6.5h11"/></svg>
      Thêm đồ uống
    </a>
  </div>

  <c:if test="${not empty sessionScope.message}">
    <div class="pc-alert success">${sessionScope.message}</div>
    <c:remove var="message" scope="session"/>
  </c:if>

  <%-- Search bar --%>
  <form method="get" action="${pageContext.request.contextPath}/manager/drinks" class="pc-search-row">
    <input type="text" name="keyword" class="pc-input" placeholder="Tên đồ uống..." value="${keyword}" style="max-width:200px;">
    <select name="categoryId" class="pc-select" style="max-width:180px;">
      <option value="0">Tất cả danh mục</option>
      <c:forEach var="cat" items="${categories}">
        <option value="${cat.id}" ${cat.id == categoryId ? 'selected' : ''}>${cat.name}</option>
      </c:forEach>
    </select>
    <select name="active" class="pc-select" style="max-width:150px;">
      <option value="-1" ${active == -1 ? 'selected' : ''}>Tất cả trạng thái</option>
      <option value="1"  ${active == 1  ? 'selected' : ''}>Hoạt động</option>
      <option value="0"  ${active == 0  ? 'selected' : ''}>Không hoạt động</option>
    </select>
    <button type="submit" class="pc-btn latte">Tìm kiếm</button>
  </form>

  <div class="pc-card">
    <div style="padding:0;">
      <table class="pc-table">
        <thead>
          <tr>
            <th style="width:56px;">ID</th>
            <th style="width:76px;">Ảnh</th>
            <th>Tên đồ uống</th>
            <th style="width:120px;">Giá</th>
            <th style="width:130px;">Danh mục</th>
            <th style="width:120px;">Trạng thái</th>
            <th style="width:140px;">Thao tác</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="d" items="${drinks}">
            <tr>
              <td style="color:rgba(44,24,16,.4);font-size:12px;">#${d.id}</td>
              <td>
                <c:choose>
                  <c:when test="${not empty d.image}">
                    <img src="${pageContext.request.contextPath}/uploads/${d.image}"
                         style="width:52px;height:52px;object-fit:cover;border-radius:8px;border:1px solid var(--steam);" alt="${d.name}">
                  </c:when>
                  <c:otherwise>
                    <div style="width:52px;height:52px;border-radius:8px;background:var(--foam);border:1px solid var(--steam);display:flex;align-items:center;justify-content:center;">
                      <svg width="20" height="20" viewBox="0 0 20 20" fill="none" stroke="rgba(44,24,16,.2)" stroke-width="1.2"><path d="M4 6h12L14 16H6L4 6z"/><path d="M7 6V5a3 3 0 016 0v1"/></svg>
                    </div>
                  </c:otherwise>
                </c:choose>
              </td>
              <td style="font-weight:500;">${d.name}</td>
              <td><fmt:formatNumber value="${d.price}" pattern="#,###"/>đ</td>
              <td style="color:rgba(44,24,16,.55);">${d.categoryId}</td>
              <td><span class="pc-pill ${d.active ? 'active' : 'off'}">${d.active ? 'Hoạt động' : 'Ẩn'}</span></td>
              <td>
                <div style="display:flex;gap:6px;">
                  <a href="?action=edit&id=${d.id}" class="pc-btn ghost sm">Sửa</a>
                  <a href="?action=delete&id=${d.id}" class="pc-btn danger sm"
                     onclick="return confirm('Xóa đồ uống «${d.name}»?')">Xóa</a>
                </div>
              </td>
            </tr>
          </c:forEach>
          <c:if test="${empty drinks}">
            <tr><td colspan="7" style="text-align:center;padding:32px;color:rgba(44,24,16,.4);">Không tìm thấy đồ uống nào</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>

  <c:if test="${totalPages > 1}">
    <div class="pc-pagination">
      <c:forEach begin="1" end="${totalPages}" var="i">
        <a href="?keyword=${keyword}&categoryId=${categoryId}&active=${active}&page=${i}"
           class="pc-page-btn ${i == currentPage ? 'active' : ''}">${i}</a>
      </c:forEach>
    </div>
  </c:if>
</main>
</body>
</html>
