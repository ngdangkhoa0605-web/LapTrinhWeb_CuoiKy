<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Trang chủ" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="store-hero mb-5">
	<div class="store-hero-content">
		<span class="hero-eyebrow"><i class="bi bi-stars"></i> MỞ RA MỘT THẾ GIỚI MỚI</span>
		<h1>Mỗi cuốn sách<br>mở một <span>chân trời.</span></h1>
		<p>Khám phá những cuốn sách được yêu thích, tuyển chọn kỹ lưỡng tại các nhà sách trong hệ thống.</p>
		<a href="${pageContext.request.contextPath}/books" class="btn btn-hero">Khám phá hiệu sách <i class="bi bi-arrow-right"></i></a>
	</div>
	<div class="hero-art" aria-hidden="true"><div class="hero-sun"></div><div class="hero-book hero-book-one"></div><div class="hero-book hero-book-two"></div><div class="hero-book hero-book-three"></div><i class="bi bi-book-half hero-icon"></i><span class="hero-caption">Một cuốn hay,<br>một ngày đẹp.</span></div>
</section>

<div class="d-flex justify-content-between align-items-center mb-3">
	<h3><i class="bi bi-stars"></i> Sách mới nhất</h3>
	<a href="${pageContext.request.contextPath}/books" class="text-decoration-none">Xem tất cả &raquo;</a>
</div>

<div class="row row-cols-1 row-cols-md-3 row-cols-lg-5 g-3 mb-5">
	<c:forEach items="${latestBooks}" var="b">
		<div class="col">
			<a href="${pageContext.request.contextPath}/books/${b.bookId}" class="text-decoration-none text-dark">
				<div class="card book-card shadow-sm">
					<c:choose><c:when test="${not empty b.image}"><img src="<c:out value='${b.image}'/>" class="card-img-top" alt="<c:out value='${b.title}'/>" loading="lazy"></c:when><c:otherwise><div class="book-cover-fallback" role="img" aria-label="Sách chưa có ảnh bìa"><i class="bi bi-book-half"></i><span>Góc đọc</span></div></c:otherwise></c:choose>
					<div class="card-body">
						<h6 class="card-title text-truncate"><c:out value="${b.title}"/></h6>
						<span class="badge bg-secondary badge-condition">${b.conditionGrade}</span>
						<div class="price mt-1"><fmt:formatNumber value="${b.price}" type="number" groupingUsed="true" />₫</div>
					</div>
				</div>
			</a>
		</div>
	</c:forEach>
	<c:if test="${empty latestBooks}">
		<div class="col-12"><div class="empty-catalog"><i class="bi bi-journal-bookmark"></i><h5>Những trang sách mới sắp xuất hiện</h5><p>Hiện chưa có sách để giới thiệu. Hãy quay lại khám phá sau nhé.</p></div></div>
	</c:if>
</div>

<div class="section-heading mb-3"><div><span class="section-kicker">TÌM ĐỌC THEO SỞ THÍCH</span><h3><i class="bi bi-tags"></i> Danh mục sách</h3></div><a href="${pageContext.request.contextPath}/books" class="section-link">Tất cả sách <i class="bi bi-arrow-right"></i></a></div>
<div class="row row-cols-2 row-cols-md-4 g-3 mb-5">
	<c:forEach items="${categories}" var="c">
		<div class="col">
			<a href="${pageContext.request.contextPath}/books?categoryId=${c.categoryId}" class="btn btn-outline-primary w-100 py-3">${c.categoryName}</a>
		</div>
	</c:forEach>
</div>

<div class="section-heading mb-3"><div><span class="section-kicker">LUÔN CÓ MỘT NHÀ SÁCH GẦN BẠN</span><h3><i class="bi bi-shop"></i> Hệ thống chi nhánh</h3></div></div>
<div class="row row-cols-1 row-cols-md-3 g-3">
	<c:forEach items="${stores}" var="s">
		<div class="col">
			<div class="card h-100">
				<div class="card-body">
					<h6 class="card-title"><i class="bi bi-geo-alt-fill text-danger"></i> <c:out value="${s.storeName}"/></h6>
					<p class="card-text small text-muted mb-1"><c:out value="${s.address}"/></p>
					<p class="card-text small text-muted"><c:out value="${s.phone}"/></p>
					<a href="${pageContext.request.contextPath}/books?storeId=${s.storeId}" class="btn btn-sm btn-outline-secondary">Xem sách tại đây</a>
				</div>
			</div>
		</div>
	</c:forEach>
	<c:if test="${empty stores}"><div class="col-12"><div class="empty-catalog"><i class="bi bi-geo-alt"></i><h5>Thông tin chi nhánh đang được cập nhật</h5><p>Vui lòng quay lại sau để tìm nhà sách gần bạn.</p></div></div></c:if>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
