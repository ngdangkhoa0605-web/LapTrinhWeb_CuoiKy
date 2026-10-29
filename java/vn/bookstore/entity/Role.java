package vn.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Vai trò người dùng: ROLE_ADMIN, ROLE_MANAGER (quản lý chi nhánh),
 * ROLE_STAFF (nhân viên chi nhánh), ROLE_CUSTOMER (khách hàng).
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "role_name", unique = true, nullable = false, length = 50)
	private String roleName;

	public Role(String roleName) {
		this.roleName = roleName;
	}
}
