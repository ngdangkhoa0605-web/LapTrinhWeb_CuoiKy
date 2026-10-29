<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Tạo tài khoản" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4">Tạo tài khoản Quản lý / Nhân viên chi nhánh</h3>

<form action="${pageContext.request.contextPath}/admin/users/save" method="post" class="col-md-6">
	<div class="mb-3">
		<label class="form-label">Tên đăng nhập *</label>
		<input type="text" class="form-control" name="username" required>
	</div>
	<div class="mb-3">
		<label class="form-label">Mật khẩu *</label>
		<input type="password" class="form-control" name="password" required>
	</div>
	<div class="mb-3">
		<label class="form-label">Email *</label>
		<input type="email" class="form-control" name="email" required>
	</div>
	<div class="mb-3">
		<label class="form-label">Họ tên</label>
		<input type="text" class="form-control" name="fullName">
	</div>
	<div class="mb-3">
		<label class="form-label">Vai trò *</label>
		<select name="roleName" class="form-select" required>
			<option value="ROLE_MANAGER">Quản lý chi nhánh (MANAGER)</option>
			<option value="ROLE_STAFF">Nhân viên chi nhánh (STAFF)</option>
			<option value="ROLE_ADMIN">Quản trị hệ thống (ADMIN)</option>
		</select>
	</div>
	<div class="mb-3">
		<label class="form-label">Chi nhánh (bắt buộc với MANAGER/STAFF)</label>
		<select name="storeId" class="form-select">
			<option value="">-- Không thuộc chi nhánh nào (dành cho ADMIN) --</option>
			<c:forEach items="${stores}" var="s">
				<option value="${s.storeId}">${s.storeName}</option>
			</c:forEach>
		</select>
	</div>
	<button type="submit" class="btn btn-primary">Tạo tài khoản</button>
	<a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Hủy</a>
</form>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
