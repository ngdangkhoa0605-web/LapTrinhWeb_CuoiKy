package vn.bookstore.exception;

/** Ngoại lệ nghiệp vụ (dữ liệu không hợp lệ, trùng lặp, không tìm thấy...). */
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BusinessException(String message) {
		super(message);
	}
}
