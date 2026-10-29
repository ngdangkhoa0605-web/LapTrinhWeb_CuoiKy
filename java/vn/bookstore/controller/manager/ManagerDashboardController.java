package vn.bookstore.controller.manager;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Store;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.security.UserPrincipal;
import vn.bookstore.service.NotificationService;

/**
 * Dashboard của Manager/Staff 1 chi nhánh - có widget hiển thị thông báo
 * đơn hàng mới REAL-TIME (kết nối WebSocket /ws, subscribe topic
 * "/topic/store/{storeId}/orders" - xem manager/dashboard.jsp).
 */
@Controller
@RequestMapping("/manager")
@RequiredArgsConstructor
public class ManagerDashboardController {

	private final NotificationService notificationService;

	@GetMapping("/dashboard")
	public String dashboard(@AuthenticationPrincipal UserPrincipal principal, Model model) {
		Store store = principal.getUser().getStore();
		if (store == null) {
			throw new BusinessException("Tài khoản chưa được gán vào chi nhánh nào. Liên hệ Admin để cấu hình.");
		}
		model.addAttribute("store", store);
		model.addAttribute("notifications", notificationService.findByStore(store.getStoreId()));
		model.addAttribute("unreadCount", notificationService.countUnread(store.getStoreId()));
		return "manager/dashboard";
	}
}
