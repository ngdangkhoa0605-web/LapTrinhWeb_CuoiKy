<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Đơn hàng của tôi" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4"><i class="bi bi-receipt"></i> Đơn hàng của tôi</h3>

<c:if test="${alert != null}">
	<div class="alert alert-success">${alert}</div>
</c:if>

<table class="table">
	<thead>
		<tr>
			<th>Mã đơn</th>
			<th>Ngày đặt</th>
			<th>Chi nhánh</th>
			<th>Tổng tiền</th>
			<th>Trạng thái</th>
			<th></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach items="${orders}" var="o">
			<tr>
				<td>${o.orderCode}</td>
				<td><fmt:formatDate value="${o.orderDate}" pattern="dd/MM/yyyy HH:mm" /></td>
				<td>${o.store.storeName}</td>
				<td><fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true" />₫</td>
				<td><span class="badge bg-info">${o.status}</span></td>
				<td><a href="${pageContext.request.contextPath}/my-orders/${o.orderId}" class="btn btn-sm btn-outline-primary">Chi tiết</a></td>
			</tr>
		</c:forEach>
		<c:if test="${empty orders}">
			<tr><td colspan="6" class="text-center text-muted">Bạn chưa có đơn hàng nào.</td></tr>
		</c:if>
	</tbody>
</table>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
