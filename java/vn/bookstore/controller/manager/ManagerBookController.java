package vn.bookstore.controller.manager;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Book;
import vn.bookstore.entity.Store;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.security.UserPrincipal;
import vn.bookstore.service.BookService;
import vn.bookstore.service.CategoryService;
import vn.bookstore.service.CloudinaryService;

/**
 * CRUD sách - Manager/Staff CHỈ được thao tác trên sách thuộc CHÍNH chi
 * nhánh của mình (kiểm tra store.getStoreId() khớp với principal.getStore()).
 */
@Controller
@RequestMapping("/manager/books")
@RequiredArgsConstructor
public class ManagerBookController {

	private static final int PAGE_SIZE = 10;

	private final BookService bookService;
	private final CategoryService categoryService;
	private final CloudinaryService cloudinaryService;

	@GetMapping
	public String list(@AuthenticationPrincipal UserPrincipal principal,
			@RequestParam(defaultValue = "0") int page, Model model) {
		Store store = requireStore(principal);
		Page<Book> bookPage = bookService.findAll(page, PAGE_SIZE, null, null, store.getStoreId());
		model.addAttribute("bookPage", bookPage);
		model.addAttribute("books", bookPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", bookPage.getTotalPages());
		return "manager/book-list";
	}

	@GetMapping("/add")
	public String addForm(Model model) {
		model.addAttribute("book", new Book());
		model.addAttribute("categories", categoryService.findActive());
		return "manager/book-form";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal principal, Model model) {
		Book book = bookService.findById(id);
		checkOwnership(book, principal);
		model.addAttribute("book", book);
		model.addAttribute("categories", categoryService.findActive());
		return "manager/book-form";
	}

	@PostMapping("/save")
	public String save(
			@AuthenticationPrincipal UserPrincipal principal,
			@RequestParam(required = false) Integer bookId,
			@RequestParam String title,
			@RequestParam(required = false) String author,
			@RequestParam(required = false) String publisher,
			@RequestParam(required = false) String isbn,
			@RequestParam(required = false) String description,
			@RequestParam java.math.BigDecimal price,
			@RequestParam(required = false) java.math.BigDecimal originalPrice,
			@RequestParam int quantity,
			@RequestParam(defaultValue = "GOOD") String conditionGrade,
			@RequestParam int categoryId,
			@RequestParam(defaultValue = "1") int status,
			@RequestParam(required = false) MultipartFile imageFile,
			RedirectAttributes redirect) {

		try {
			Store store = requireStore(principal);
			Book book;
			if (bookId != null) {
				book = bookService.findById(bookId);
				checkOwnership(book, principal);
			} else {
				book = new Book();
				book.setStore(store);
			}

			book.setTitle(title);
			book.setAuthor(author);
			book.setPublisher(publisher);
			book.setIsbn(isbn);
			book.setDescription(description);
			book.setPrice(price);
			book.setOriginalPrice(originalPrice);
			book.setQuantity(quantity);
			book.setConditionGrade(conditionGrade);
			book.setStatus(status);
			book.setCategory(categoryService.findById(categoryId));

			if (imageFile != null && !imageFile.isEmpty()) {
				book.setImage(cloudinaryService.uploadImage(imageFile, "books"));
			}

			bookService.save(book);
			redirect.addFlashAttribute("alert", "Lưu sách thành công!");
		} catch (BusinessException | IOException e) {
			redirect.addFlashAttribute("alert", "Lỗi: " + e.getMessage());
		}
		return "redirect:/manager/books";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable Integer id, @AuthenticationPrincipal UserPrincipal principal,
			RedirectAttributes redirect) {
		Book book = bookService.findById(id);
		checkOwnership(book, principal);
		bookService.delete(id);
		redirect.addFlashAttribute("alert", "Đã xóa sách!");
		return "redirect:/manager/books";
	}

	private Store requireStore(UserPrincipal principal) {
		Store store = principal.getUser().getStore();
		if (store == null) {
			throw new BusinessException("Tài khoản chưa được gán vào chi nhánh nào!");
		}
		return store;
	}

	private void checkOwnership(Book book, UserPrincipal principal) {
		Store myStore = requireStore(principal);
		boolean isAdmin = principal.getAuthorities().stream()
				.anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
		if (!isAdmin && !book.getStore().getStoreId().equals(myStore.getStoreId())) {
			throw new BusinessException("Bạn không có quyền thao tác trên sách của chi nhánh khác!");
		}
	}
}
