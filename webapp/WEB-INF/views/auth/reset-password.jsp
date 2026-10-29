<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Đặt lại mật khẩu" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
	<div class="col-md-5">
		<div class="card shadow-sm">
			<div class="card-body p-4">
				<h3 class="text-center mb-3"><i class="bi bi-key-fill"></i> Đặt lại mật khẩu</h3>
				<p>Mã OTP đã được gửi tới email <b>${email}</b>. Mã có hiệu lực trong <b>5 phút</b>.</p>

				<c:if test="${alert != null}">
					<div class="alert alert-danger">${alert}</div>
				</c:if>

				<form action="${pageContext.request.contextPath}/reset-password" method="post">
					<input type="hidden" name="email" value="${email}">
					<div class="mb-3">
						<label class="form-label">Mã OTP</label>
						<input type="text" class="form-control" name="otp" maxlength="6" required autofocus>
					</div>
					<div class="mb-3">
						<label class="form-label">Mật khẩu mới</label>
						<input type="password" class="form-control" name="newPassword" required>
					</div>
					<div class="mb-3">
						<label class="form-label">Nhập lại mật khẩu mới</label>
						<input type="password" class="form-control" name="renewPassword" required>
					</div>
					<button type="submit" class="btn btn-primary w-100">Đặt lại mật khẩu</button>
				</form>
				<p class="text-center mt-3 mb-0">
					Không nhận được mã?
					<a href="${pageContext.request.contextPath}/reset-password?email=${email}&resend=1">Gửi lại OTP</a>
				</p>
			</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
