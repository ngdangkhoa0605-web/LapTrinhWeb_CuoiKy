<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title><c:out value="${pageTitle != null ? pageTitle : 'Bookstore Chain Management'}" /></title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css" rel="stylesheet">
<link href="${pageContext.request.contextPath}/css/style.css" rel="stylesheet">
</head>
<body>
	<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
		<div class="container">
			<a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/home">
				<i class="bi bi-book-half"></i> Bookstore Chain
			</a>
			<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="collapse navbar-collapse" id="mainNav">
				<ul class="navbar-nav me-auto">
					<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
					<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/books">Sách</a></li>
					<sec:authorize access="hasRole('ADMIN')">
						<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Trang quản trị</a></li>
					</sec:authorize>
					<sec:authorize access="hasAnyRole('MANAGER','STAFF')">
						<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/manager/dashboard">Quản lý chi nhánh</a></li>
					</sec:authorize>
					<sec:authorize access="hasRole('CUSTOMER')">
						<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/my-orders">Đơn hàng của tôi</a></li>
					</sec:authorize>
				</ul>
				<ul class="navbar-nav">
					<sec:authorize access="hasRole('CUSTOMER')">
						<li class="nav-item">
							<a class="nav-link" href="${pageContext.request.contextPath}/cart"><i class="bi bi-cart3"></i> Giỏ hàng</a>
						</li>
					</sec:authorize>
					<sec:authorize access="isAuthenticated()">
						<li class="nav-item">
							<span class="nav-link text-light">
								<i class="bi bi-person-circle"></i> <sec:authentication property="principal.user.fullName" />
							</span>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
						</li>
					</sec:authorize>
					<sec:authorize access="isAnonymous()">
						<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/login">Đăng nhập</a></li>
						<li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/register">Đăng ký</a></li>
					</sec:authorize>
				</ul>
			</div>
		</div>
	</nav>
	<div class="container my-4">
