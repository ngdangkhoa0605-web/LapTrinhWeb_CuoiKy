package vn.bookstore.controller.admin;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Order;
import vn.bookstore.service.OrderService;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    /**
     * =====================================================
     * DANH SÁCH ĐƠN HÀNG
     * =====================================================
     */
    @GetMapping
    public String listOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            Model model) {

        Page<Order> orderPage =
                orderService.findForAdmin(
                        page,
                        size,
                        status
                );

        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("orderPage", orderPage);
        model.addAttribute("status", status);

        // Danh sách status cho select
        model.addAttribute(
                "statuses",
                new String[] {
                        Order.PENDING,
                        Order.CONFIRMED,
                        Order.SHIPPING,
                        Order.COMPLETED,
                        Order.CANCELLED
                }
        );

        return "admin/orders/list";
    }

    /**
     * =====================================================
     * CHI TIẾT ĐƠN HÀNG
     * =====================================================
     */
    @GetMapping("/{id}")
    public String detail(
            @PathVariable Integer id,
            Model model) {

        Order order =
                orderService.findById(id);

        model.addAttribute("order", order);

        model.addAttribute(
                "statuses",
                new String[] {
                        Order.PENDING,
                        Order.CONFIRMED,
                        Order.SHIPPING,
                        Order.COMPLETED,
                        Order.CANCELLED
                }
        );

        return "admin/orders/detail";
    }

    /**
     * =====================================================
     * CẬP NHẬT TRẠNG THÁI
     * =====================================================
     */
    @PostMapping("/{id}/status")
    public String updateStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes) {

        try {

            orderService.updateStatus(
                    id,
                    status
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Cập nhật trạng thái đơn hàng thành công."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/orders/" + id;
    }
}
