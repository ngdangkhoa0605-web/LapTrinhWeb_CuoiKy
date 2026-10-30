<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set var="pageTitle" value="Tất cả sách" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp"%>

<div class="row">
	<div class="col-md-3">
		<div class="card shadow-sm mb-3">
			<div class="card-body">
				<h6 class="card-title">Tìm kiếm</h6>
				<form method="get" action="${pageContext.request.contextPath}/books">
					<input type="text" class="form-control mb-2" name="keyword"
						value="<c:out value='${keyword}'/>" placeholder="Tên sách...">
					<button type="submit" class="btn btn-primary btn-sm w-100">Tìm</button>
				</form>
			</div>
		</div>
		<div class="card shadow-sm mb-3">
			<div class="card-body">
				<h6 class="card-title">Danh mục</h6>
				<div class="list-group list-group-flush">
					<a
						class="list-group-item list-group-item-action ${empty categoryId ? 'active' : ''}"
						href="${pageContext.request.contextPath}/books">Tất cả</a>
					<c:forEach items="${categories}" var="c">
						<a
							class="list-group-item list-group-item-action ${categoryId == c.categoryId ? 'active' : ''}"
							href="${pageContext.request.contextPath}/books?categoryId=${c.categoryId}">${c.categoryName}</a>
					</c:forEach>
				</div>
			</div>
		</div>
		<div class="card shadow-sm">
			<div class="card-body">
				<h6 class="card-title">Chi nhánh</h6>
				<div class="list-group list-group-flush">
					<a
						class="list-group-item list-group-item-action ${empty storeId ? 'active' : ''}"
						href="${pageContext.request.contextPath}/books">Tất cả</a>
					<c:forEach items="${stores}" var="s">
						<a
							class="list-group-item list-group-item-action ${storeId == s.storeId ? 'active' : ''}"
							href="${pageContext.request.contextPath}/books?storeId=${s.storeId}">${s.storeName}</a>
					</c:forEach>
				</div>
			</div>
		</div>
	</div>

	<div class="col-md-9">
		<h4 class="mb-3">
			Tất cả sách <small class="text-muted">(${bookPage.totalElements}
				kết quả)</small>
		</h4>

		<div class="row row-cols-1 row-cols-md-3 g-3">
			<c:forEach items="${books}" var="b">
				<div class="col">
					<a href="${pageContext.request.contextPath}/books/${b.bookId}"
						class="text-decoration-none text-dark">
						<div class="card book-card shadow-sm">
							<c:choose><c:when test="${not empty b.image}"><img src="<c:out value='${b.image}'/>" class="card-img-top" alt="<c:out value='${b.title}'/>" loading="lazy"></c:when><c:otherwise><div class="book-cover-fallback" role="img" aria-label="Sách chưa có ảnh bìa"><i class="bi bi-book-half"></i><span>Góc đọc</span></div></c:otherwise></c:choose>
							<div class="card-body">
								<h6 class="card-title text-truncate"><c:out value="${b.title}"/></h6>
								<p class="small text-muted mb-1"><c:out value="${b.category.categoryName}"/>
									- <c:out value="${b.store.storeName}"/></p>
								<span class="badge bg-secondary badge-condition">${b.conditionGrade}</span>
								<div class="price mt-1">
									<fmt:formatNumber value="${b.price}" type="number"
										groupingUsed="true" />
									₫
								</div>
							</div>
						</div>
					</a>
				</div>
			</c:forEach>
			<c:if test="${empty books}">
				<div class="col-12"><div class="empty-catalog"><i class="bi bi-search"></i><h5>Chưa tìm thấy cuốn sách phù hợp</h5><p>Hãy thử từ khóa khác hoặc xóa bộ lọc để xem toàn bộ sách.</p><a class="btn btn-outline-primary btn-sm mt-3" href="${pageContext.request.contextPath}/books">Xóa bộ lọc</a></div></div>
			</c:if>
		</div>

		<c:if test="${totalPages > 1}">
			<nav class="mt-4">
				<ul class="pagination justify-content-center">

					<c:forEach begin="0" end="${totalPages - 1}" var="i">

						<li class="page-item ${i == currentPage ? 'active' : ''}"><a
							class="page-link"
							href="${pageContext.request.contextPath}/books?page=${i}<c:if test='${not empty keyword}'>&keyword=${keyword}</c:if><c:if test='${not empty categoryId}'>&categoryId=${categoryId}</c:if><c:if test='${not empty storeId}'>&storeId=${storeId}</c:if>">
								${i + 1} </a></li>

					</c:forEach>

				</ul>
			</nav>
		</c:if>
	</div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp"%>
