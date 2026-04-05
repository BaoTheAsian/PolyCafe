<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>PolyCoffee — Đăng nhập</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@500;600&family=DM+Sans:wght@300;400;500&display=swap" rel="stylesheet">
  <style>
    *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }
    :root {
      --cream: #FAF6F0; --espresso: #2C1810; --latte: #C8956C;
      --foam: #F5EDE0; --steam: #E8DDD0; --warm-red: #C04B3E;
    }
    body {
      font-family: 'DM Sans', sans-serif;
      background: var(--espresso);
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      overflow: hidden;
    }
    /* Decorative rings */
    body::before {
      content: '';
      position: absolute; top: -120px; right: -120px;
      width: 400px; height: 400px; border-radius: 50%;
      border: 60px solid rgba(200,149,108,.07);
    }
    body::after {
      content: '';
      position: absolute; bottom: -80px; left: -80px;
      width: 280px; height: 280px; border-radius: 50%;
      border: 40px solid rgba(200,149,108,.05);
    }
    .login-wrap {
      width: 100%; max-width: 380px; padding: 0 20px;
      position: relative; z-index: 1;
    }
    .brand {
      text-align: center; margin-bottom: 32px;
    }
    .brand-cup {
      width: 54px; height: 54px; background: var(--latte);
      border-radius: 50% 50% 45% 45%;
      display: flex; align-items: center; justify-content: center;
      margin: 0 auto 14px; position: relative;
    }
    .brand-cup::after {
      content: ''; position: absolute; top: -8px; right: -6px;
      width: 14px; height: 14px; border: 3px solid var(--latte);
      border-radius: 0 50% 50% 0; border-left: none;
    }
    .brand-title {
      font-family: 'Playfair Display', serif;
      font-size: 28px; font-weight: 600; color: var(--foam);
      letter-spacing: .5px;
    }
    .brand-sub { font-size: 12px; color: rgba(255,255,255,.35); margin-top: 4px; letter-spacing: 2px; text-transform: uppercase; }
    .login-card {
      background: var(--foam);
      border-radius: 18px;
      padding: 32px 28px 28px;
    }
    .card-heading {
      font-family: 'Playfair Display', serif;
      font-size: 19px; font-weight: 500; color: var(--espresso);
      margin-bottom: 6px;
    }
    .card-sub { font-size: 13px; color: rgba(44,24,16,.45); margin-bottom: 26px; }
    .alert-danger {
      background: rgba(192,75,62,.08); border: 1px solid rgba(192,75,62,.22);
      color: var(--warm-red); border-radius: 9px; padding: 9px 14px;
      font-size: 13px; margin-bottom: 18px;
    }
    .form-group { margin-bottom: 16px; }
    label { display:block; font-size:12px; font-weight:500; color:rgba(44,24,16,.6); margin-bottom:6px; letter-spacing:.3px; }
    input[type=email], input[type=password] {
      width:100%; padding:10px 14px; border:1px solid var(--steam);
      border-radius:10px; font-family:'DM Sans',sans-serif; font-size:14px;
      color:var(--espresso); background:#fff; outline:none;
      transition:border-color .14s, box-shadow .14s;
    }
    input:focus { border-color:var(--latte); box-shadow:0 0 0 3px rgba(200,149,108,.12); }
    .submit-btn {
      width:100%; padding:11px; border:none; border-radius:10px;
      background:var(--espresso); color:var(--foam); font-family:'DM Sans',sans-serif;
      font-size:14px; font-weight:500; cursor:pointer; margin-top:6px;
      transition:background .15s;
    }
    .submit-btn:hover { background:#1a0e08; }
    .login-footer { text-align:center; margin-top:18px; font-size:12px; color:rgba(255,255,255,.28); }
  </style>
</head>
<body>
<div class="login-wrap">
  <div class="brand">
    <div class="brand-cup">
      <svg width="24" height="24" viewBox="0 0 24 24" fill="none">
        <path d="M4 8h16l-2 11H6L4 8z" fill="rgba(255,255,255,0.92)"/>
        <path d="M9 8V5a3 3 0 016 0v3" stroke="rgba(255,255,255,0.5)" stroke-width="1.5" fill="none"/>
      </svg>
    </div>
    <div class="brand-title">PolyCoffee</div>
    <div class="brand-sub">Management System</div>
  </div>

  <div class="login-card">
    <div class="card-heading">Chào mừng trở lại</div>
    <div class="card-sub">Đăng nhập để tiếp tục quản lý</div>

    <c:if test="${not empty error}">
      <div class="alert-danger">${error}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/auth/login">
      <input type="hidden" name="_csrf" value="${sessionScope._csrf}">
      <div class="form-group">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" value="${email}" required placeholder="admin@polycoffee.com">
      </div>
      <div class="form-group">
        <label for="password">Mật khẩu</label>
        <input type="password" id="password" name="password" required placeholder="••••••">
      </div>
      <button type="submit" class="submit-btn">Đăng nhập →</button>
    </form>
  </div>

  <div class="login-footer">PolyCoffee © 2026 · FPT Polytechnic PRO1041</div>
</div>
</body>
</html>
