<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Tất cả đơn hàng" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4"><i class="bi bi-receipt"></i> Tất cả đơn hàng (toàn hệ thống)</h3>

<table class="table table-hover align-middle">
	<thead>
		<tr><th>Mã đơn</th><th>Khách hàng</th><th>Chi nhánh</th><th>Ngày đặt</th><th>Tổng tiền</th><th>Trạng thái</th></tr>
	</thead>
	<tbody>
		<c:forEach items="${orders}" var="o">
			<tr>
				<td>${o.orderCode}</td>
				<td>${o.customer.fullName} (${o.customer.username})</td>
				<td>${o.store.storeName}</td>
				<td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm" /></td>
				<td><fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true" />₫</td>
				<td><span class="badge bg-info">${o.status}</span></td>
			</tr>
		</c:forEach>
		<c:if test="${empty orders}">
			<tr><td colspan="6" class="text-center text-muted">Chưa có đơn hàng nào.</td></tr>
		</c:if>
	</tbody>
</table>

<nav>
	<ul class="pagination justify-content-center">
		<c:forEach begin="0" end="${totalPages - 1}" var="i">
			<li class="page-item ${i == currentPage ? 'active' : ''}">
				<a class="page-link" href="${pageContext.request.contextPath}/admin/orders?page=${i}">${i + 1}</a>
			</li>
		</c:forEach>
	</ul>
</nav>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
