package vn.bookstore.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Áp dụng cho các Controller trả về JSP (web/admin/manager) - KHÁC với
 * GlobalApiExceptionHandler (chỉ áp dụng cho "/api/**" trả JSON).
 */
@ControllerAdvice(basePackages = { "vn.bookstore.controller.web", "vn.bookstore.controller.admin",
		"vn.bookstore.controller.manager" })
public class GlobalWebExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public String handleBusiness(BusinessException e, Model model) {
		model.addAttribute("errorMessage", e.getMessage());
		return "common/error";
	}
}
