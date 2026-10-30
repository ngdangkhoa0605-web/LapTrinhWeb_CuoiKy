<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý chi nhánh" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
	<h3><i class="bi bi-shop"></i> Quản lý chi nhánh</h3>
	<a href="${pageContext.request.contextPath}/admin/stores/add" class="btn btn-primary">+ Thêm chi nhánh</a>
</div>

<c:if test="${alert != null}">
	<div class="alert alert-info">${alert}</div>
</c:if>

<table class="table table-hover align-middle">
	<thead>
		<tr><th>#</th><th>Tên chi nhánh</th><th>Địa chỉ</th><th>SĐT</th><th>Trạng thái</th><th></th></tr>
	</thead>
	<tbody>
		<c:forEach items="${stores}" var="s" varStatus="st">
			<tr>
				<td>${st.index + 1}</td>
				<td>${s.storeName}</td>
				<td>${s.address}</td>
				<td>${s.phone}</td>
				<td>
					<c:choose>
						<c:when test="${s.status == 1}"><span class="badge bg-success">Hoạt động</span></c:when>
						<c:otherwise><span class="badge bg-secondary">Ngưng</span></c:otherwise>
					</c:choose>
				</td>
				<td>
					<a href="${pageContext.request.contextPath}/admin/stores/edit/${s.storeId}" class="btn btn-sm btn-outline-primary">Sửa</a>
					<a href="${pageContext.request.contextPath}/admin/stores/delete/${s.storeId}" class="btn btn-sm btn-outline-danger"
						onclick="return confirm('Xóa chi nhánh này?');">Xóa</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
