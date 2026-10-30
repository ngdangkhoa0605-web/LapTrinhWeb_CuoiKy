<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Xử lý đơn hàng" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-3"><i class="bi bi-receipt"></i> Đơn hàng #${order.orderCode}</h3>

<c:if test="${alert != null}">
	<div class="alert alert-success">${alert}</div>
</c:if>

<div class="row mb-4">
	<div class="col-md-6">
		<p><b>Khách hàng:</b> ${order.customer.fullName} (${order.customer.username})</p>
		<p><b>Ngày đặt:</b> <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" /></p>
		<p><b>Trạng thái hiện tại:</b> <span class="badge bg-info">${order.status}</span></p>
	</div>
	<div class="col-md-6">
		<p><b>Địa chỉ giao hàng:</b> ${order.shippingAddress}</p>
		<p><b>SĐT nhận hàng:</b> ${order.receiverPhone}</p>
		<p><b>Thanh toán:</b> ${order.paymentMethod}</p>
	</div>
</div>

<table class="table mb-4">
	<thead><tr><th>Sách</th><th>Đơn giá</th><th>SL</th><th>Thành tiền</th></tr></thead>
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

<form action="${pageContext.request.contextPath}/manager/orders/${order.orderId}/status" method="post" class="d-flex gap-2 col-md-6">
	<select name="status" class="form-select">
		<option value="PENDING" ${order.status == 'PENDING' ? 'selected' : ''}>Chờ xử lý</option>
		<option value="CONFIRMED" ${order.status == 'CONFIRMED' ? 'selected' : ''}>Đã xác nhận</option>
		<option value="SHIPPING" ${order.status == 'SHIPPING' ? 'selected' : ''}>Đang giao hàng</option>
		<option value="COMPLETED" ${order.status == 'COMPLETED' ? 'selected' : ''}>Hoàn tất</option>
		<option value="CANCELLED" ${order.status == 'CANCELLED' ? 'selected' : ''}>Đã hủy</option>
	</select>
	<button type="submit" class="btn btn-primary">Cập nhật trạng thái</button>
</form>

<a href="${pageContext.request.contextPath}/manager/orders" class="btn btn-secondary mt-3">&laquo; Quay lại danh sách</a>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
