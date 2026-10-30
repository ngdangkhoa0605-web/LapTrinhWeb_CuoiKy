package vn.bookstore.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.bookstore.dto.NotificationPayload;
import vn.bookstore.entity.Notification;
import vn.bookstore.entity.Order;
import vn.bookstore.repository.NotificationRepository;
import vn.bookstore.service.NotificationService;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

	private final NotificationRepository notificationRepository;
	private final SimpMessagingTemplate messagingTemplate;

	@Override
	public void notifyNewOrder(Order order) {
		String title = "Đơn hàng mới #" + order.getOrderCode();
		String message = "Khách hàng " + order.getCustomer().getFullName() + " vừa đặt đơn trị giá "
				+ order.getTotalAmount() + "đ tại chi nhánh " + order.getStore().getStoreName();

		// 1) Lưu lại DB để hiển thị lịch sử thông báo trên dashboard
		Notification notification = new Notification(order.getStore(), title, message, "NEW_ORDER");
		notificationRepository.save(notification);

		// 2) Đẩy REAL-TIME qua WebSocket tới đúng topic của chi nhánh đó
		NotificationPayload payload = new NotificationPayload(
				title, message, "NEW_ORDER", order.getOrderId(), order.getOrderCode(), LocalDateTime.now());

		String destination = "/topic/store/" + order.getStore().getStoreId() + "/orders";
		messagingTemplate.convertAndSend(destination, payload);
	}

	@Override
	public List<Notification> findByStore(Integer storeId) {
		return notificationRepository.findByStore_StoreIdOrderByCreatedDateDesc(storeId);
	}

	@Override
	public long countUnread(Integer storeId) {
		return notificationRepository.countByStore_StoreIdAndIsReadFalse(storeId);
	}

	@Override
	public void markAllAsRead(Integer storeId) {
		List<Notification> list = findByStore(storeId);
		list.forEach(n -> n.setRead(true));
		notificationRepository.saveAll(list);
	}
}
