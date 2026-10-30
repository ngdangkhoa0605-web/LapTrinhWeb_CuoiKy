<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="${book.bookId != null ? 'Sửa' : 'Thêm'} sách" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h3 class="mb-4">${book.bookId != null ? 'Chỉnh sửa' : 'Thêm mới'} sách</h3>

<form action="${pageContext.request.contextPath}/manager/books/save" method="post" enctype="multipart/form-data" class="col-md-7">
	<input type="hidden" name="bookId" value="${book.bookId}">
	<div class="mb-3">
		<label class="form-label">Tên sách *</label>
		<input type="text" class="form-control" name="title" value="${book.title}" required>
	</div>
	<div class="row">
		<div class="col-md-6 mb-3">
			<label class="form-label">Tác giả</label>
			<input type="text" class="form-control" name="author" value="${book.author}">
		</div>
		<div class="col-md-6 mb-3">
			<label class="form-label">Nhà xuất bản</label>
			<input type="text" class="form-control" name="publisher" value="${book.publisher}">
		</div>
	</div>
	<div class="mb-3">
		<label class="form-label">ISBN</label>
		<input type="text" class="form-control" name="isbn" value="${book.isbn}">
	</div>
	<div class="mb-3">
		<label class="form-label">Danh mục *</label>
		<select name="categoryId" class="form-select" required>
			<c:forEach items="${categories}" var="c">
				<option value="${c.categoryId}" ${book.category != null && book.category.categoryId == c.categoryId ? 'selected' : ''}>
					${c.categoryName}
				</option>
			</c:forEach>
		</select>
	</div>
	<div class="row">
		<div class="col-md-4 mb-3">
			<label class="form-label">Giá bán *</label>
			<input type="number" step="1000" min="0" class="form-control" name="price" value="${book.price}" required>
		</div>
		<div class="col-md-4 mb-3">
			<label class="form-label">Giá gốc (khi mới)</label>
			<input type="number" step="1000" min="0" class="form-control" name="originalPrice" value="${book.originalPrice}">
		</div>
		<div class="col-md-4 mb-3">
			<label class="form-label">Số lượng tồn kho *</label>
			<input type="number" min="0" class="form-control" name="quantity" value="${book.quantity}" required>
		</div>
	</div>
	<div class="mb-3">
		<label class="form-label">Tình trạng sách cũ</label>
		<select name="conditionGrade" class="form-select">
			<option value="NEW" ${book.conditionGrade == 'NEW' ? 'selected' : ''}>Mới (chưa qua sử dụng)</option>
			<option value="LIKE_NEW" ${book.conditionGrade == 'LIKE_NEW' ? 'selected' : ''}>Như mới</option>
			<option value="GOOD" ${book.conditionGrade == 'GOOD' || book.conditionGrade == null ? 'selected' : ''}>Tốt</option>
			<option value="FAIR" ${book.conditionGrade == 'FAIR' ? 'selected' : ''}>Cũ, còn dùng tốt</option>
		</select>
	</div>
	<div class="mb-3">
		<label class="form-label">Mô tả</label>
		<textarea class="form-control" name="description" rows="4">${book.description}</textarea>
	</div>
	<div class="mb-3">
		<c:if test="${book.image != null}">
			<img src="${book.image}" width="120" class="d-block mb-2 rounded">
		</c:if>
		<label class="form-label">Ảnh sách (upload lên Cloudinary)</label>
		<input type="file" class="form-control" name="imageFile" accept="image/*">
	</div>
	<div class="mb-3">
		<label class="form-label">Trạng thái</label>
		<select name="status" class="form-select">
			<option value="1" ${book.status == 1 ? 'selected' : ''}>Đang bán</option>
			<option value="0" ${book.status == 0 ? 'selected' : ''}>Ngừng bán</option>
		</select>
	</div>
	<button type="submit" class="btn btn-primary">Lưu</button>
	<a href="${pageContext.request.contextPath}/manager/books" class="btn btn-secondary">Hủy</a>
</form>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
