package vn.bookstore.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Người dùng hệ thống: ADMIN (toàn quyền), MANAGER/STAFF (gắn với 1 Store cụ
 * thể), CUSTOMER (khách mua hàng, store = null).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Integer userId;

	@NotEmpty(message = "Tên đăng nhập không được để rỗng")
	@Column(name = "username", unique = true, nullable = false, length = 100)
	private String username;

	@NotEmpty(message = "Mật khẩu không được để rỗng")
	@Column(name = "password", nullable = false, length = 255)
	private String password; // đã băm bằng BCrypt

	@Email(message = "Email không hợp lệ")
	@NotEmpty(message = "Email không được để rỗng")
	@Column(name = "email", unique = true, nullable = false, length = 255)
	private String email;

	@Column(name = "full_name", length = 255)
	private String fullName;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "address", length = 500)
	private String address;

	@Column(name = "avatar", length = 500)
	private String avatar;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", nullable = false)
	private Role role;

	/** Chi nhánh làm việc (chỉ áp dụng cho MANAGER/STAFF, null với ADMIN/CUSTOMER) */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "store_id", nullable = true)
	private Store store;

	/** 0 = chưa kích hoạt (chờ xác thực OTP), 1 = đã kích hoạt, 2 = bị khóa */
	@Column(name = "status")
	private int status = 0;

	@Column(name = "otp", length = 10)
	private String otp;

	@Column(name = "otp_expire")
	private LocalDateTime otpExpire;

	@Column(name = "created_date")
	private LocalDateTime createdDate = LocalDateTime.now();

	public boolean isActive() {
		return status == 1;
	}
}
