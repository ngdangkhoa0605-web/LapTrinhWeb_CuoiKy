<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="pageTitle" value="${book.title}" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:if test="${alert != null}">
	<div class="alert alert-info">${alert}</div>
</c:if>

<div class="row">
	<div class="col-md-5">
		<c:choose><c:when test="${not empty book.image}"><img src="<c:out value='${book.image}'/>" class="img-fluid rounded shadow-sm" alt="<c:out value='${book.title}'/>" loading="lazy"></c:when><c:otherwise><div class="book-cover-fallback detail-cover" role="img" aria-label="Sách chưa có ảnh bìa"><i class="bi bi-book-half"></i><span>Góc đọc</span></div></c:otherwise></c:choose>
	</div>
	<div class="col-md-7">
		<h2><c:out value="${book.title}"/></h2>
		<p class="text-muted mb-1"><i class="bi bi-person"></i> Tác giả: ${book.author}</p>
		<p class="text-muted mb-1"><i class="bi bi-building"></i> NXB: ${book.publisher}</p>
		<p class="text-muted mb-1"><i class="bi bi-tag"></i> Danh mục: ${book.category.categoryName}</p>
		<p class="text-muted mb-3"><i class="bi bi-shop"></i> Chi nhánh: ${book.store.storeName} - ${book.store.address}</p>

		<span class="badge bg-secondary mb-2">Tình trạng: ${book.conditionGrade}</span>

		<h3 class="price">
			<fmt:formatNumber value="${book.price}" type="number" groupingUsed="true" />₫
			<c:if test="${book.originalPrice != null}">
				<small class="text-muted text-decoration-line-through fs-6 ms-2">
					<fmt:formatNumber value="${book.originalPrice}" type="number" groupingUsed="true" />₫
				</small>
			</c:if>
		</h3>

		<c:choose>
			<c:when test="${book.quantity > 0}">
				<p class="text-success"><i class="bi bi-check-circle"></i> Còn hàng (${book.quantity} cuốn)</p>
			</c:when>
			<c:otherwise>
				<p class="text-danger"><i class="bi bi-x-circle"></i> Hết hàng</p>
			</c:otherwise>
		</c:choose>

		<sec:authorize access="hasRole('CUSTOMER')">
			<form action="${pageContext.request.contextPath}/cart/add" method="post" class="d-flex gap-2 mt-3">
				<input type="hidden" name="bookId" value="${book.bookId}">
				<input type="number" name="quantity" value="1" min="1" max="${book.quantity}" class="form-control" style="width:100px;">
				<button type="submit" class="btn btn-primary" ${book.quantity == 0 ? 'disabled' : ''}>
					<i class="bi bi-cart-plus"></i> Thêm vào giỏ
				</button>
			</form>
		</sec:authorize>
		<sec:authorize access="isAnonymous()">
			<a href="${pageContext.request.contextPath}/login" class="btn btn-primary mt-3">Đăng nhập để mua hàng</a>
		</sec:authorize>

		<hr>
		<h5>Mô tả sản phẩm</h5>
		<p style="white-space:pre-line;"><c:out value="${book.description}"/></p>
	</div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
