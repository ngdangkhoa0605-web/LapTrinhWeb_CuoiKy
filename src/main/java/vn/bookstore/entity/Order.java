package vn.bookstore.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 1 đơn hàng luôn thuộc về đúng 1 Store cụ thể (vì hệ thống multi-store: nếu
 * giỏ hàng có sách ở nhiều chi nhánh khác nhau, khi checkout sẽ được TÁCH
 * thành nhiều Order riêng theo từng Store - xem CheckoutService).
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String PENDING = "PENDING";
	public static final String CONFIRMED = "CONFIRMED";
	public static final String SHIPPING = "SHIPPING";
	public static final String COMPLETED = "COMPLETED";
	public static final String CANCELLED = "CANCELLED";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;

	@Column(name = "order_code", unique = true, length = 50)
	private String orderCode;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "customer_id", nullable = false)
	private User customer;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "order_date")
	private LocalDateTime orderDate = LocalDateTime.now();

	@Column(name = "status", length = 20)
	private String status = PENDING;

	@Column(name = "total_amount", precision = 14, scale = 2)
	private BigDecimal totalAmount = BigDecimal.ZERO;

	@Column(name = "shipping_address", length = 500)
	private String shippingAddress;

	@Column(name = "receiver_phone", length = 20)
	private String receiverPhone;

	@Column(name = "payment_method", length = 30)
	private String paymentMethod = "COD";

	@Column(name = "note", length = 500)
	private String note;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<OrderDetail> orderDetails = new ArrayList<>();
}
