<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Trang quản trị" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4"><i class="bi bi-speedometer2"></i> Trang quản trị hệ thống</h3>

<div class="row g-3 mb-4">
	<div class="col-md-4">
		<div class="card text-bg-primary shadow-sm">
			<div class="card-body">
				<h6>Tổng số chi nhánh</h6>
				<h2>${totalStores}</h2>
				<a href="${pageContext.request.contextPath}/admin/stores" class="text-white">Quản lý &raquo;</a>
			</div>
		</div>
	</div>
	<div class="col-md-4">
		<div class="card text-bg-success shadow-sm">
			<div class="card-body">
				<h6>Tổng số danh mục</h6>
				<h2>${totalCategories}</h2>
				<a href="${pageContext.request.contextPath}/admin/categories" class="text-white">Quản lý &raquo;</a>
			</div>
		</div>
	</div>
	<div class="col-md-4">
		<div class="card text-bg-warning shadow-sm">
			<div class="card-body">
				<h6>Tổng số người dùng</h6>
				<h2>${totalUsers}</h2>
				<a href="${pageContext.request.contextPath}/admin/users" class="text-dark">Quản lý &raquo;</a>
			</div>
		</div>
	</div>
</div>

<div class="list-group">
	<a href="${pageContext.request.contextPath}/admin/stores" class="list-group-item list-group-item-action">
		<i class="bi bi-shop"></i> Quản lý chi nhánh (thêm/sửa/xóa)
	</a>
	<a href="${pageContext.request.contextPath}/admin/categories" class="list-group-item list-group-item-action">
		<i class="bi bi-tags"></i> Quản lý danh mục sách
	</a>
	<a href="${pageContext.request.contextPath}/admin/users" class="list-group-item list-group-item-action">
		<i class="bi bi-people"></i> Quản lý tài khoản (tạo tài khoản Quản lý/Nhân viên chi nhánh)
	</a>
	<a href="${pageContext.request.contextPath}/admin/orders" class="list-group-item list-group-item-action">
		<i class="bi bi-receipt"></i> Xem toàn bộ đơn hàng (tất cả chi nhánh)
	</a>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
