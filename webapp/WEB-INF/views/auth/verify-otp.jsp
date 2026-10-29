<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Xác thực OTP" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="row justify-content-center">
	<div class="col-md-5">
		<div class="card shadow-sm">
			<div class="card-body p-4 text-center">
				<h3 class="mb-3"><i class="bi bi-shield-check"></i> Xác thực OTP</h3>
				<p>Mã OTP kích hoạt tài khoản đã được gửi tới email bạn đã đăng ký.<br>
					Mã có hiệu lực trong <b>5 phút</b>.</p>

				<c:if test="${alert != null}">
					<div class="alert alert-info">${alert}</div>
				</c:if>

				<form action="${pageContext.request.contextPath}/verify-otp" method="post">
					<input type="hidden" name="username" value="${username}">
					<input type="text" class="form-control form-control-lg text-center mb-3"
						name="otp" maxlength="6" placeholder="- - - - - -" style="letter-spacing:8px;" required autofocus>
					<button type="submit" class="btn btn-primary w-100">Xác nhận kích hoạt</button>
				</form>
				<p class="mt-3 mb-0">
					Không nhận được mã?
					<a href="${pageContext.request.contextPath}/verify-otp?username=${username}&resend=1">Gửi lại OTP</a>
				</p>
			</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
