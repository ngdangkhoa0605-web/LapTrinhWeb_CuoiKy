<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<c:set var="pageTitle" value="Đăng nhập" scope="request" />

<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div class="row justify-content-center">

	<div class="col-md-5">

		<div class="card shadow-sm">

			<div class="card-body p-4">

				<h3 class="text-center mb-4">
					<i class="bi bi-box-arrow-in-right"></i> Đăng nhập
				</h3>

				<c:if test="${param.activated == '1'}">
					<div class="alert alert-success">Kích hoạt tài khoản thành
						công! Vui lòng đăng nhập.</div>
				</c:if>

				<c:if test="${param.resetok == '1'}">
					<div class="alert alert-success">Đặt lại mật khẩu thành công!
					</div>
				</c:if>

				<c:if test="${param.logout == '1'}">
					<div class="alert alert-info">Bạn đã đăng xuất.</div>
				</c:if>

				<!-- Xử lý lỗi đăng nhập -->
				<c:if test="${param.error == '1'}">
					<div class="alert alert-danger">Tài khoản hoặc mật khẩu không
						đúng!</div>
				</c:if>

				<form action="${pageContext.request.contextPath}/login"
					method="post">

					<div class="mb-3">
						<label class="form-label">Tài khoản</label> <input type="text"
							class="form-control" name="username" value="${param.username}"
							required autofocus>
					</div>

					<div class="mb-3">
						<label class="form-label">Mật khẩu</label> <input type="password"
							class="form-control" name="password" required>
					</div>

					<div class="d-flex justify-content-between mb-3">

						<a href="${pageContext.request.contextPath}/forgot-password"
							class="small"> Quên mật khẩu? </a>

					</div>

					<button type="submit" class="btn btn-primary w-100">Đăng
						nhập</button>

				</form>

				<p class="text-center mt-3 mb-0">
					Chưa có tài khoản? <a
						href="${pageContext.request.contextPath}/register"> Đăng ký
						ngay </a>
				</p>

			</div>

		</div>

	</div>

</div>

<%@ include file="/WEB-INF/views/common/footer.jsp"%>