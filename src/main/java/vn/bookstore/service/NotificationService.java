package vn.bookstore.service;

import java.util.List;

import vn.bookstore.entity.Notification;
import vn.bookstore.entity.Order;

public interface NotificationService {
	/** Tạo thông báo "đơn hàng mới" cho chi nhánh xử lý đơn + đẩy real-time qua WebSocket. */
	void notifyNewOrder(Order order);

	List<Notification> findByStore(Integer storeId);
	long countUnread(Integer storeId);
	void markAllAsRead(Integer storeId);
}
