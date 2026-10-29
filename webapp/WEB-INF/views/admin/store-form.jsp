<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${store.storeId != null ? 'Sửa' : 'Thêm'} chi nhánh" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4">${store.storeId != null ? 'Chỉnh sửa' : 'Thêm mới'} chi nhánh</h3>

<form action="${pageContext.request.contextPath}/admin/stores/save" method="post" enctype="multipart/form-data" class="col-md-6">
	<input type="hidden" name="storeId" value="${store.storeId}">
	<div class="mb-3">
		<label class="form-label">Tên chi nhánh *</label>
		<input type="text" class="form-control" name="storeName" value="${store.storeName}" required>
	</div>
	<div class="mb-3">
		<label class="form-label">Địa chỉ</label>
		<input type="text" class="form-control" name="address" value="${store.address}">
	</div>
	<div class="mb-3">
		<label class="form-label">Số điện thoại</label>
		<input type="text" class="form-control" name="phone" value="${store.phone}">
	</div>
	<div class="mb-3">
		<c:if test="${store.image != null}">
			<img src="${store.image}" width="150" class="d-block mb-2 rounded">
		</c:if>
		<label class="form-label">Ảnh chi nhánh (upload lên Cloudinary)</label>
		<input type="file" class="form-control" name="imageFile" accept="image/*">
	</div>
	<div class="mb-3">
		<label class="form-label">Trạng thái</label>
		<select name="status" class="form-select">
			<option value="1" ${store.status == 1 ? 'selected' : ''}>Hoạt động</option>
			<option value="0" ${store.status == 0 ? 'selected' : ''}>Ngưng hoạt động</option>
		</select>
	</div>
	<button type="submit" class="btn btn-primary">Lưu</button>
	<a href="${pageContext.request.contextPath}/admin/stores" class="btn btn-secondary">Hủy</a>
</form>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
