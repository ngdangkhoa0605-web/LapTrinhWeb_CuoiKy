package vn.bookstore.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	@NotEmpty(message = "Tên đăng nhập không được để rỗng")
	private String username;

	@NotEmpty(message = "Mật khẩu không được để rỗng")
	private String password;
}
