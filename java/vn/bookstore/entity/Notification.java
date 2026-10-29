package vn.bookstore.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Thông báo real-time (đẩy qua WebSocket) cho Manager/Staff của 1 Store khi
 * có đơn hàng mới, đồng thời lưu lại DB để hiển thị lịch sử thông báo.
 */
@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notification implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notification_id")
	private Integer notificationId;

	/** Chi nhánh nhận thông báo (Manager/Staff của store này sẽ thấy) */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "title", length = 255)
	private String title;

	@Column(name = "message", length = 1000)
	private String message;

	/** NEW_ORDER, ORDER_CANCELLED, ... */
	@Column(name = "type", length = 30)
	private String type;

	@Column(name = "is_read")
	private boolean isRead = false;

	@Column(name = "created_date")
	private LocalDateTime createdDate = LocalDateTime.now();

	public Notification(Store store, String title, String message, String type) {
		this.store = store;
		this.title = title;
		this.message = message;
		this.type = type;
	}
}
