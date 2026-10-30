<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đăng ký" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
	<div class="col-md-6">
		<div class="card shadow-sm">
			<div class="card-body p-4">
				<h3 class="text-center mb-4"><i class="bi bi-person-plus"></i> Tạo tài khoản mới</h3>

				<c:if test="${alert != null}">
					<div class="alert alert-danger">${alert}</div>
				</c:if>

				<form action="${pageContext.request.contextPath}/register" method="post">
					<div class="mb-3">
						<label class="form-label">Tên đăng nhập *</label>
						<input type="text" class="form-control" name="username" required>
					</div>
					<div class="mb-3">
						<label class="form-label">Họ tên</label>
						<input type="text" class="form-control" name="fullName">
					</div>
					<div class="mb-3">
						<label class="form-label">Email * (dùng để nhận mã OTP kích hoạt)</label>
						<input type="email" class="form-control" name="email" required>
					</div>
					<div class="mb-3">
						<label class="form-label">Số điện thoại</label>
						<input type="text" class="form-control" name="phone">
					</div>
					<div class="mb-3">
						<label class="form-label">Mật khẩu *</label>
						<input type="password" class="form-control" name="password" required>
					</div>
					<button type="submit" class="btn btn-primary w-100">Tạo tài khoản</button>
				</form>
				<p class="text-center mt-3 mb-0">
					Đã có tài khoản? <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
				</p>
			</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
