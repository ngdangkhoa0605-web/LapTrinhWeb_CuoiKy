package vn.bookstore.service;

import java.util.List;

import org.springframework.data.domain.Page;

import vn.bookstore.dto.CheckoutRequest;
import vn.bookstore.entity.Order;
import vn.bookstore.entity.User;

public interface OrderService {
	/**
	 * Chuyển giỏ hàng thành đơn hàng. Vì hệ thống multi-store, nếu giỏ hàng có
	 * sách thuộc nhiều chi nhánh khác nhau, sẽ tự động TÁCH thành nhiều Order
	 * riêng biệt (mỗi Order chỉ thuộc 1 Store) để chi nhánh nào xử lý đơn của
	 * chi nhánh đó.
	 *
	 * @return danh sách các Order vừa được tạo (thường là 1, có thể nhiều hơn)
	 */
	List<Order> checkout(User customer, CheckoutRequest request);

	List<Order> findByCustomer(Integer customerId);
	Order findById(Integer orderId);
	Page<Order> findByStore(Integer storeId, String status, int page, int size);
	Page<Order> findAllForAdmin(int page, int size);
	Order updateStatus(Integer orderId, String newStatus);
}
