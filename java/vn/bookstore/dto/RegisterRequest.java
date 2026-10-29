package vn.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	@NotEmpty(message = "Tên đăng nhập không được để rỗng")
	private String username;

	@NotEmpty(message = "Mật khẩu không được để rỗng")
	private String password;

	@NotEmpty(message = "Email không được để rỗng")
	@Email(message = "Email không hợp lệ")
	private String email;

	private String fullName;
	private String phone;
}
