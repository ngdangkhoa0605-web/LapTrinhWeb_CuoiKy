package vn.bookstore.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckoutRequest {
	@NotEmpty(message = "Vui lòng nhập địa chỉ giao hàng")
	private String shippingAddress;

	@NotEmpty(message = "Vui lòng nhập số điện thoại nhận hàng")
	private String receiverPhone;

	private String paymentMethod = "COD";
	private String note;
}
