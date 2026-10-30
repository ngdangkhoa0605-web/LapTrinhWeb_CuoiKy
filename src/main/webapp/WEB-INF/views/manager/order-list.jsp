<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Quản lý đơn hàng" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4"><i class="bi bi-receipt"></i> Đơn hàng của chi nhánh</h3>

<div class="mb-3 btn-group">
	<a href="${pageContext.request.contextPath}/manager/orders" class="btn btn-outline-secondary ${empty status ? 'active' : ''}">Tất cả</a>
	<a href="${pageContext.request.contextPath}/manager/orders?status=PENDING" class="btn btn-outline-secondary ${status == 'PENDING' ? 'active' : ''}">Chờ xử lý</a>
	<a href="${pageContext.request.contextPath}/manager/orders?status=CONFIRMED" class="btn btn-outline-secondary ${status == 'CONFIRMED' ? 'active' : ''}">Đã xác nhận</a>
	<a href="${pageContext.request.contextPath}/manager/orders?status=SHIPPING" class="btn btn-outline-secondary ${status == 'SHIPPING' ? 'active' : ''}">Đang giao</a>
	<a href="${pageContext.request.contextPath}/manager/orders?status=COMPLETED" class="btn btn-outline-secondary ${status == 'COMPLETED' ? 'active' : ''}">Hoàn tất</a>
</div>

<table class="table table-hover align-middle">
	<thead>
		<tr><th>Mã đơn</th><th>Khách hàng</th><th>Ngày đặt</th><th>Tổng tiền</th><th>Trạng thái</th><th></th></tr>
	</thead>
	<tbody>
		<c:forEach items="${orders}" var="o">
			<tr>
				<td>${o.orderCode}</td>
				<td>${o.customer.fullName} - ${o.receiverPhone}</td>
				<td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm" /></td>
				<td><fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true" />₫</td>
				<td><span class="badge bg-info">${o.status}</span></td>
				<td><a href="${pageContext.request.contextPath}/manager/orders/${o.orderId}" class="btn btn-sm btn-outline-primary">Xử lý</a></td>
			</tr>
		</c:forEach>
		<c:if test="${empty orders}">
			<tr><td colspan="6" class="text-center text-muted">Không có đơn hàng nào.</td></tr>
		</c:if>
	</tbody>
</table>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
