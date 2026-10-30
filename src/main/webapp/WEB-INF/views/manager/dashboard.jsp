<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Dashboard chi nhánh" scope="request" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-4">
	<h3><i class="bi bi-speedometer2"></i> Dashboard - ${store.storeName}</h3>
	<div>
		<span class="badge bg-danger" id="connectionStatus">Đang kết nối...</span>
	</div>
</div>

<div class="row mb-4">
	<div class="col-md-4">
		<div class="card shadow-sm">
			<div class="card-body">
				<h6>Thông báo chưa đọc</h6>
				<h2 id="unreadCount">${unreadCount}</h2>
			</div>
		</div>
	</div>
</div>

<div class="list-group mb-4">
	<a href="${pageContext.request.contextPath}/manager/books" class="list-group-item list-group-item-action">
		<i class="bi bi-book"></i> Quản lý sách của chi nhánh
	</a>
	<a href="${pageContext.request.contextPath}/manager/orders" class="list-group-item list-group-item-action">
		<i class="bi bi-receipt"></i> Quản lý đơn hàng của chi nhánh
	</a>
</div>

<h5><i class="bi bi-bell"></i> Thông báo gần đây (real-time)</h5>
<div class="list-group" id="notificationList">
	<c:forEach items="${notifications}" var="n">
		<div class="list-group-item">
			<div class="d-flex justify-content-between">
				<b>${n.title}</b>
				<small class="text-muted"><fmt:formatDate value="${n.createdDate}" pattern="dd/MM HH:mm" /></small>
			</div>
			<p class="mb-0">${n.message}</p>
		</div>
	</c:forEach>
	<c:if test="${empty notifications}">
		<p class="text-muted">Chưa có thông báo nào.</p>
	</c:if>
</div>

<!-- Toast hiển thị thông báo real-time -->
<div id="notifToast" class="toast align-items-center text-bg-primary border-0" role="alert">
	<div class="d-flex">
		<div class="toast-body" id="toastBody">...</div>
		<button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
	</div>
</div>

<!-- Thư viện SockJS + STOMP để kết nối WebSocket -->
<script src="https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@stomp/stompjs@7.0.0/bundles/stomp.umd.min.js"></script>
<script>
	const storeId = ${store.storeId};
	const contextPath = "${pageContext.request.contextPath}";

	const stompClient = new StompJs.Client({
		webSocketFactory: () => new SockJS(contextPath + "/ws"),
		reconnectDelay: 5000,
		onConnect: () => {
			document.getElementById("connectionStatus").textContent = "Đã kết nối real-time";
			document.getElementById("connectionStatus").className = "badge bg-success";

			// Subscribe đúng topic của chi nhánh mình đang quản lý
			stompClient.subscribe("/topic/store/" + storeId + "/orders", (message) => {
				const payload = JSON.parse(message.body);
				showToast(payload);
				prependNotification(payload);
				incrementUnreadCount();
			});
		},
		onDisconnect: () => {
			document.getElementById("connectionStatus").textContent = "Mất kết nối";
			document.getElementById("connectionStatus").className = "badge bg-secondary";
		}
	});
	stompClient.activate();

	function showToast(payload) {
		document.getElementById("toastBody").innerHTML = "<b>" + payload.title + "</b><br>" + payload.message;
		const toast = new bootstrap.Toast(document.getElementById("notifToast"));
		toast.show();
	}

	function prependNotification(payload) {
		const list = document.getElementById("notificationList");
		const item = document.createElement("div");
		item.className = "list-group-item list-group-item-warning";
		item.innerHTML = "<div class='d-flex justify-content-between'><b>" + payload.title +
			"</b><small>vừa xong</small></div><p class='mb-0'>" + payload.message + "</p>";
		list.prepend(item);
	}

	function incrementUnreadCount() {
		const el = document.getElementById("unreadCount");
		el.textContent = parseInt(el.textContent) + 1;
	}
</script>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
