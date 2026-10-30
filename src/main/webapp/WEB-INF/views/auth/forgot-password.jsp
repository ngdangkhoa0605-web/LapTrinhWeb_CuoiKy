<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quên mật khẩu" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
	<div class="col-md-5">
		<div class="card shadow-sm">
			<div class="card-body p-4">
				<h3 class="text-center mb-3"><i class="bi bi-key"></i> Quên mật khẩu</h3>
				<p>Nhập email đã đăng ký, hệ thống sẽ gửi mã OTP để đặt lại mật khẩu.</p>

				<c:if test="${alert != null}">
					<div class="alert alert-danger">${alert}</div>
				</c:if>

				<form action="${pageContext.request.contextPath}/forgot-password" method="post">
					<div class="mb-3">
						<input type="email" class="form-control" name="email" placeholder="Email đã đăng ký" required autofocus>
					</div>
					<button type="submit" class="btn btn-primary w-100">Gửi mã OTP</button>
				</form>
				<p class="text-center mt-3 mb-0">
					<a href="${pageContext.request.contextPath}/login">&laquo; Quay lại đăng nhập</a>
				</p>
			</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
