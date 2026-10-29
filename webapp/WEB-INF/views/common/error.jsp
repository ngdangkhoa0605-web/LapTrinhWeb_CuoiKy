<%@ include file="/WEB-INF/views/common/header.jsp" %>
<div class="text-center py-5">
	<i class="bi bi-exclamation-triangle text-danger" style="font-size:4rem;"></i>
	<h2 class="mt-3">Đã có lỗi xảy ra</h2>
	<p class="text-muted">${errorMessage}</p>
	<a href="${pageContext.request.contextPath}/home" class="btn btn-primary mt-2">Về trang chủ</a>
</div>
<%@ include file="/WEB-INF/views/common/footer.jsp" %>
