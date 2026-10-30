<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Quản lý sách" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
	<h3><i class="bi bi-book"></i> Quản lý sách chi nhánh</h3>
	<a href="${pageContext.request.contextPath}/manager/books/add" class="btn btn-primary">+ Thêm sách</a>
</div>

<c:if test="${alert != null}">
	<div class="alert alert-info">${alert}</div>
</c:if>

<table class="table table-hover align-middle">
	<thead>
		<tr><th>#</th><th>Ảnh</th><th>Tên sách</th><th>Danh mục</th><th>Giá</th><th>Tồn kho</th><th>Trạng thái</th><th></th></tr>
	</thead>
	<tbody>
		<c:forEach items="${books}" var="b" varStatus="st">
			<tr>
				<td>${st.index + 1}</td>
				<td>
					<c:if test="${b.image != null}">
						<img src="${b.image}" width="50" height="60" style="object-fit:cover;" class="rounded">
					</c:if>
				</td>
				<td>${b.title}</td>
				<td>${b.category.categoryName}</td>
				<td><fmt:formatNumber value="${b.price}" type="number" groupingUsed="true" />₫</td>
				<td>${b.quantity}</td>
				<td>
					<c:choose>
						<c:when test="${b.status == 1}"><span class="badge bg-success">Đang bán</span></c:when>
						<c:otherwise><span class="badge bg-secondary">Ngừng bán</span></c:otherwise>
					</c:choose>
				</td>
				<td>
					<a href="${pageContext.request.contextPath}/manager/books/edit/${b.bookId}" class="btn btn-sm btn-outline-primary">Sửa</a>
					<a href="${pageContext.request.contextPath}/manager/books/delete/${b.bookId}" class="btn btn-sm btn-outline-danger"
						onclick="return confirm('Xóa sách này?');">Xóa</a>
				</td>
			</tr>
		</c:forEach>
		<c:if test="${empty books}">
			<tr><td colspan="8" class="text-center text-muted">Chi nhánh chưa có sách nào.</td></tr>
		</c:if>
	</tbody>
</table>

<c:if test="${totalPages > 0}">
    <nav>
        <ul class="pagination justify-content-center">
            <c:forEach begin="0" end="${totalPages - 1}" var="i">
                <li class="page-item ${i == currentPage ? 'active' : ''}">
                    <a class="page-link"
                       href="${pageContext.request.contextPath}/manager/books?page=${i}">
                        ${i + 1}
                    </a>
                </li>
            </c:forEach>
        </ul>
    </nav>
</c:if>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
