package vn.bookstore.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.bookstore.dto.CheckoutRequest;
import vn.bookstore.entity.Book;
import vn.bookstore.entity.Cart;
import vn.bookstore.entity.CartItem;
import vn.bookstore.entity.Order;
import vn.bookstore.entity.OrderDetail;
import vn.bookstore.entity.Store;
import vn.bookstore.entity.User;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.repository.BookRepository;
import vn.bookstore.repository.CartItemRepository;
import vn.bookstore.repository.CartRepository;
import vn.bookstore.repository.OrderRepository;
import vn.bookstore.service.NotificationService;
import vn.bookstore.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final OrderRepository orderRepository;
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final BookRepository bookRepository;
	private final NotificationService notificationService;

	@Override
	@Transactional
	public List<Order> checkout(User customer, CheckoutRequest request) {
		Cart cart = cartRepository.findByUser_UserId(customer.getUserId())
				.orElseThrow(() -> new BusinessException("Giỏ hàng trống, không thể đặt hàng"));

		List<CartItem> items = cart.getItems();
		if (items.isEmpty()) {
			throw new BusinessException("Giỏ hàng trống, không thể đặt hàng");
		}

		// Kiểm tra tồn kho TRƯỚC khi trừ, tránh trường hợp đơn hàng thứ 2 bị trừ âm
		for (CartItem item : items) {
			Book book = item.getBook();
			if (book.getQuantity() < item.getQuantity()) {
				throw new BusinessException(
						"Sách \"" + book.getTitle() + "\" chỉ còn " + book.getQuantity() + " cuốn trong kho");
			}
		}

		// Gom nhóm CartItem theo Store -> mỗi Store sẽ tạo thành 1 Order riêng
		Map<Integer, List<CartItem>> itemsByStore = new HashMap<>();
		for (CartItem item : items) {
			Integer storeId = item.getBook().getStore().getStoreId();
			itemsByStore.computeIfAbsent(storeId, k -> new ArrayList<>()).add(item);
		}

		List<Order> createdOrders = new ArrayList<>();

		for (Map.Entry<Integer, List<CartItem>> entry : itemsByStore.entrySet()) {
			List<CartItem> storeItems = entry.getValue();
			Store store = storeItems.get(0).getBook().getStore();

			Order order = new Order();
			order.setOrderCode(generateOrderCode());
			order.setCustomer(customer);
			order.setStore(store);
			order.setOrderDate(LocalDateTime.now());
			order.setStatus(Order.PENDING);
			order.setShippingAddress(request.getShippingAddress());
			order.setReceiverPhone(request.getReceiverPhone());
			order.setPaymentMethod(request.getPaymentMethod());
			order.setNote(request.getNote());

			BigDecimal total = BigDecimal.ZERO;
			List<OrderDetail> details = new ArrayList<>();
			for (CartItem item : storeItems) {
				Book book = item.getBook();
				OrderDetail detail = new OrderDetail(order, book, item.getQuantity(), book.getPrice());
				details.add(detail);
				total = total.add(detail.getSubTotal());

				// Trừ tồn kho ngay khi đặt hàng
				book.setQuantity(book.getQuantity() - item.getQuantity());
				bookRepository.save(book);
			}
			order.setOrderDetails(details);
			order.setTotalAmount(total);

			order = orderRepository.save(order);
			createdOrders.add(order);

			// Đẩy thông báo real-time cho chi nhánh xử lý đơn này
			notificationService.notifyNewOrder(order);
		}

		// Xóa sạch giỏ hàng sau khi đặt hàng thành công
		cartItemRepository.deleteAll(items);

		return createdOrders;
	}

	@Override
	public List<Order> findByCustomer(Integer customerId) {
		return orderRepository.findByCustomer_UserIdOrderByOrderDateDesc(customerId);
	}

	@Override
	public Order findById(Integer orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new BusinessException("Không tìm thấy đơn hàng có id = " + orderId));
	}

	@Override
	public Page<Order> findByStore(Integer storeId, String status, int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by(Sort.Direction.DESC, "orderDate"));
		if (status != null && !status.isBlank()) {
			return orderRepository.findByStore_StoreIdAndStatusOrderByOrderDateDesc(storeId, status, pageable);
		}
		return orderRepository.findByStore_StoreIdOrderByOrderDateDesc(storeId, pageable);
	}

	@Override
	public Page<Order> findAllForAdmin(int page, int size) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by(Sort.Direction.DESC, "orderDate"));
		return orderRepository.findAllByOrderByOrderDateDesc(pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<Order> findForAdmin(int page, int size, String status) {

		if (page < 0) {
			page = 0;
		}

		if (size <= 0) {
			size = 10;
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));

		String searchStatus = null;

		if (status != null && !status.trim().isEmpty()) {
			searchStatus = status.trim().toUpperCase();
		}

		return orderRepository.findForAdmin(searchStatus, pageable);
	}

	@Override
	@Transactional
	public Order updateStatus(Integer orderId, String newStatus) {
		Order order = findById(orderId);
		order.setStatus(newStatus);
		return orderRepository.save(order);
	}

	private String generateOrderCode() {
		String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
		return "ORD" + datePart;
	}
}
