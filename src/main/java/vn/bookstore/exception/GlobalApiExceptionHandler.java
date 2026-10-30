package vn.bookstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import vn.bookstore.dto.ApiResponse;

/**
 * Chỉ áp dụng cho tầng REST API ("/api/**"). Các Controller JSP tự xử lý
 * lỗi bằng try/catch + flash attribute (xem controller/web, controller/admin).
 */
@RestControllerAdvice(basePackages = "vn.bookstore.controller.api")
public class GlobalApiExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Object>> handleBusiness(BusinessException e) {
		return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(FieldError::getDefaultMessage)
				.orElse("Dữ liệu không hợp lệ");
		return ResponseEntity.badRequest().body(ApiResponse.fail(message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleOther(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.fail("Lỗi hệ thống: " + e.getMessage()));
	}
}
