<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Thanh toán" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4"><i class="bi bi-bag-check"></i> Xác nhận đơn hàng</h3>

<c:if test="${alert != null}">
	<div class="alert alert-danger">${alert}</div>
</c:if>

<div class="row">
	<div class="col-md-7">
		<div class="card shadow-sm">
			<div class="card-body">
				<h5 class="card-title mb-3">Thông tin giao hàng</h5>
				<form action="${pageContext.request.contextPath}/checkout" method="post">
					<div class="mb-3">
						<label class="form-label">Địa chỉ giao hàng *</label>
						<input type="text" name="shippingAddress" class="form-control" required>
					</div>
					<div class="mb-3">
						<label class="form-label">Số điện thoại nhận hàng *</label>
						<input type="text" name="receiverPhone" class="form-control" required>
					</div>
					<div class="mb-3">
						<label class="form-label">Phương thức thanh toán</label>
						<select name="paymentMethod" class="form-select">
							<option value="COD">Thanh toán khi nhận hàng (COD)</option>
							<option value="BANK_TRANSFER">Chuyển khoản ngân hàng</option>
						</select>
					</div>
					<div class="mb-3">
						<label class="form-label">Ghi chú</label>
						<textarea name="note" class="form-control" rows="2"></textarea>
					</div>
					<div class="alert alert-info small">
						<i class="bi bi-info-circle"></i> Nếu giỏ hàng của bạn có sách thuộc nhiều chi nhánh khác nhau,
						hệ thống sẽ TỰ ĐỘNG tách thành nhiều đơn hàng riêng biệt theo từng chi nhánh.
					</div>
					<button type="submit" class="btn btn-success w-100">Đặt hàng</button>
				</form>
			</div>
		</div>
	</div>

	<div class="col-md-5">
		<div class="card shadow-sm">
			<div class="card-body">
				<h5 class="card-title mb-3">Đơn hàng của bạn</h5>
				<c:set var="grandTotal" value="${0}" />
				<c:forEach items="${cart.items}" var="item">
					<c:set var="subTotal" value="${item.book.price * item.quantity}" />
					<c:set var="grandTotal" value="${grandTotal + subTotal}" />
					<div class="d-flex justify-content-between border-bottom py-2">
						<span>${item.book.title} x${item.quantity}</span>
						<span><fmt:formatNumber value="${subTotal}" type="number" groupingUsed="true" />₫</span>
					</div>
				</c:forEach>
				<div class="d-flex justify-content-between pt-3">
					<strong>Tổng cộng</strong>
					<strong class="price"><fmt:formatNumber value="${grandTotal}" type="number" groupingUsed="true" />₫</strong>
				</div>
			</div>
		</div>
	</div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
