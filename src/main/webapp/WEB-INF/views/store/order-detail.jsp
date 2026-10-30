<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Chi tiết đơn hàng" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-3"><i class="bi bi-receipt"></i> Đơn hàng #${order.orderCode}</h3>

<div class="row mb-4">
	<div class="col-md-6">
		<p><b>Ngày đặt:</b> <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" /></p>
		<p><b>Chi nhánh xử lý:</b> ${order.store.storeName} - ${order.store.address}</p>
		<p><b>Trạng thái:</b> <span class="badge bg-info">${order.status}</span></p>
	</div>
	<div class="col-md-6">
		<p><b>Địa chỉ giao hàng:</b> ${order.shippingAddress}</p>
		<p><b>Số điện thoại:</b> ${order.receiverPhone}</p>
		<p><b>Phương thức thanh toán:</b> ${order.paymentMethod}</p>
	</div>
</div>

<table class="table">
	<thead>
		<tr><th>Sách</th><th>Đơn giá</th><th>Số lượng</th><th>Thành tiền</th></tr>
	</thead>
	<tbody>
		<c:forEach items="${order.orderDetails}" var="d">
			<tr>
				<td>${d.book.title}</td>
				<td><fmt:formatNumber value="${d.price}" type="number" groupingUsed="true" />₫</td>
				<td>${d.quantity}</td>
				<td><fmt:formatNumber value="${d.subTotal}" type="number" groupingUsed="true" />₫</td>
			</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3" class="text-end"><b>Tổng cộng</b></td>
			<td><b class="price"><fmt:formatNumber value="${order.totalAmount}" type="number" groupingUsed="true" />₫</b></td>
		</tr>
	</tfoot>
</table>

<a href="${pageContext.request.contextPath}/my-orders" class="btn btn-secondary">&laquo; Quay lại danh sách</a>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
