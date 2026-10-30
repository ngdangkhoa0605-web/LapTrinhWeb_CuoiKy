<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý danh mục" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
	<h3><i class="bi bi-tags"></i> Quản lý danh mục</h3>
	<a href="${pageContext.request.contextPath}/admin/categories/add" class="btn btn-primary">+ Thêm danh mục</a>
</div>

<c:if test="${alert != null}">
	<div class="alert alert-info">${alert}</div>
</c:if>

<form method="get" action="${pageContext.request.contextPath}/admin/categories" class="mb-3 col-md-4">
	<div class="input-group">
		<input type="text" class="form-control" name="keyword" value="${keyword}" placeholder="Tìm theo tên...">
		<button type="submit" class="btn btn-outline-secondary">Tìm</button>
	</div>
</form>

<table class="table table-hover align-middle">
	<thead><tr><th>#</th><th>Ảnh</th><th>Tên danh mục</th><th>Mô tả</th><th>Trạng thái</th><th></th></tr></thead>
	<tbody>
		<c:forEach items="${categories}" var="c" varStatus="st">
			<tr>
				<td>${st.index + 1}</td>
				<td>
					<c:if test="${c.image != null}">
						<img src="${c.image}" width="60" height="60" style="object-fit:cover;" class="rounded">
					</c:if>
				</td>
				<td>${c.categoryName}</td>
				<td>${c.description}</td>
				<td>
					<c:choose>
						<c:when test="${c.status == 1}"><span class="badge bg-success">Hiển thị</span></c:when>
						<c:otherwise><span class="badge bg-secondary">Ẩn</span></c:otherwise>
					</c:choose>
				</td>
				<td>
					<a href="${pageContext.request.contextPath}/admin/categories/edit/${c.categoryId}" class="btn btn-sm btn-outline-primary">Sửa</a>
					<a href="${pageContext.request.contextPath}/admin/categories/delete/${c.categoryId}" class="btn btn-sm btn-outline-danger"
						onclick="return confirm('Xóa danh mục này?');">Xóa</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
