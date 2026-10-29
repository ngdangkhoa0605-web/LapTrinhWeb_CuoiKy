package vn.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
	private String token;
	private String tokenType = "Bearer";
	private String username;
	private String role;
	private Integer userId;
	private Integer storeId;

	public JwtResponse(String token, String username, String role, Integer userId, Integer storeId) {
		this.token = token;
		this.username = username;
		this.role = role;
		this.userId = userId;
		this.storeId = storeId;
	}
}
