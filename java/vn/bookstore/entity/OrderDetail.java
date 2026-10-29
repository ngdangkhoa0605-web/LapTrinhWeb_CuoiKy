package vn.bookstore.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
public class OrderDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_detail_id")
	private Integer orderDetailId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(name = "quantity")
	private int quantity;

	/** Giá tại thời điểm mua (lưu lại để không bị ảnh hưởng nếu sau này đổi giá sách) */
	@Column(name = "price", precision = 12, scale = 2)
	private BigDecimal price;

	public OrderDetail(Order order, Book book, int quantity, BigDecimal price) {
		this.order = order;
		this.book = book;
		this.quantity = quantity;
		this.price = price;
	}

	public BigDecimal getSubTotal() {
		return price.multiply(BigDecimal.valueOf(quantity));
	}
}
