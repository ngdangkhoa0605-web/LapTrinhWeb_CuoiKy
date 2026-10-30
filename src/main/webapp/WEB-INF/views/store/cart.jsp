<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Giỏ hàng" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4"><i class="bi bi-cart3"></i> Giỏ hàng của bạn</h3>

<c:if test="${alert != null}">
	<div class="alert alert-info">${alert}</div>
</c:if>

<c:choose>
	<c:when test="${empty cart.items}">
		<div class="text-center py-5">
			<i class="bi bi-cart-x" style="font-size:3rem;" class="text-muted"></i>
			<p class="mt-3">Giỏ hàng của bạn đang trống.</p>
			<a href="${pageContext.request.contextPath}/books" class="btn btn-primary">Tiếp tục mua sắm</a>
		</div>
	</c:when>
	<c:otherwise>
		<table class="table align-middle">
			<thead>
				<tr>
					<th>Sách</th>
					<th>Chi nhánh</th>
					<th>Đơn giá</th>
					<th style="width:140px;">Số lượng</th>
					<th>Thành tiền</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:set var="grandTotal" value="${0}" />
				<c:forEach items="${cart.items}" var="item">
					<c:set var="subTotal" value="${item.book.price * item.quantity}" />
					<c:set var="grandTotal" value="${grandTotal + subTotal}" />
					<tr>
						<td>
							<a href="${pageContext.request.contextPath}/books/${item.book.bookId}" class="text-decoration-none text-dark">
								${item.book.title}
							</a>
						</td>
						<td>${item.book.store.storeName}</td>
						<td><fmt:formatNumber value="${item.book.price}" type="number" groupingUsed="true" />₫</td>
						<td>
							<form action="${pageContext.request.contextPath}/cart/update/${item.cartItemId}" method="post" class="d-flex">
								<input type="number" name="quantity" value="${item.quantity}" min="1" max="${item.book.quantity}" class="form-control form-control-sm me-1">
								<button type="submit" class="btn btn-sm btn-outline-secondary">Cập nhật</button>
							</form>
						</td>
						<td><fmt:formatNumber value="${subTotal}" type="number" groupingUsed="true" />₫</td>
						<td>
							<form action="${pageContext.request.contextPath}/cart/remove/${item.cartItemId}" method="post">
								<button type="submit" class="btn btn-sm btn-outline-danger"><i class="bi bi-trash"></i></button>
							</form>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

		<div class="d-flex justify-content-end align-items-center gap-3">
			<h5 class="mb-0">Tổng cộng: <span class="price"><fmt:formatNumber value="${grandTotal}" type="number" groupingUsed="true" />₫</span></h5>
			<a href="${pageContext.request.contextPath}/checkout" class="btn btn-success">Tiến hành đặt hàng &raquo;</a>
		</div>
	</c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
