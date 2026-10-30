<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${category.categoryId != null ? 'Sửa' : 'Thêm'} danh mục" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4">${category.categoryId != null ? 'Chỉnh sửa' : 'Thêm mới'} danh mục</h3>

<form action="${pageContext.request.contextPath}/admin/categories/save" method="post" enctype="multipart/form-data" class="col-md-6">
	<input type="hidden" name="categoryId" value="${category.categoryId}">
	<div class="mb-3">
		<label class="form-label">Tên danh mục *</label>
		<input type="text" class="form-control" name="categoryName" value="${category.categoryName}" required>
	</div>
	<div class="mb-3">
		<label class="form-label">Mô tả</label>
		<textarea class="form-control" name="description" rows="3">${category.description}</textarea>
	</div>
	<div class="mb-3">
		<c:if test="${category.image != null}">
			<img src="${category.image}" width="120" class="d-block mb-2 rounded">
		</c:if>
		<label class="form-label">Ảnh danh mục (upload lên Cloudinary)</label>
		<input type="file" class="form-control" name="imageFile" accept="image/*">
	</div>
	<div class="mb-3">
		<label class="form-label">Trạng thái</label>
		<select name="status" class="form-select">
			<option value="1" ${category.status == 1 ? 'selected' : ''}>Hiển thị</option>
			<option value="0" ${category.status == 0 ? 'selected' : ''}>Ẩn</option>
		</select>
	</div>
	<button type="submit" class="btn btn-primary">Lưu</button>
	<a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Hủy</a>
</form>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
