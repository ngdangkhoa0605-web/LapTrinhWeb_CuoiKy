<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Quản lý người dùng" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
	<h3><i class="bi bi-people"></i> Quản lý tài khoản</h3>
	<a href="${pageContext.request.contextPath}/admin/users/add" class="btn btn-primary">+ Tạo tài khoản Quản lý/Nhân viên</a>
</div>

<c:if test="${alert != null}">
	<div class="alert alert-info">${alert}</div>
</c:if>

<table class="table table-hover align-middle">
	<thead>
		<tr><th>#</th><th>Tài khoản</th><th>Họ tên</th><th>Email</th><th>Vai trò</th><th>Chi nhánh</th><th>Trạng thái</th><th></th></tr>
	</thead>
	<tbody>
		<c:forEach items="${users}" var="u" varStatus="st">
			<tr>
				<td>${st.index + 1}</td>
				<td>${u.username}</td>
				<td>${u.fullName}</td>
				<td>${u.email}</td>
				<td><span class="badge bg-dark">${u.role.roleName}</span></td>
				<td>${u.store != null ? u.store.storeName : '-'}</td>
				<td>
					<c:choose>
						<c:when test="${u.status == 1}"><span class="badge bg-success">Kích hoạt</span></c:when>
						<c:when test="${u.status == 2}"><span class="badge bg-danger">Đã khóa</span></c:when>
						<c:otherwise><span class="badge bg-secondary">Chưa kích hoạt</span></c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${u.status == 2}">
							<a href="${pageContext.request.contextPath}/admin/users/unlock/${u.userId}" class="btn btn-sm btn-outline-success">Mở khóa</a>
						</c:when>
						<c:otherwise>
							<a href="${pageContext.request.contextPath}/admin/users/lock/${u.userId}" class="btn btn-sm btn-outline-danger"
								onclick="return confirm('Khóa tài khoản này?');">Khóa</a>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
