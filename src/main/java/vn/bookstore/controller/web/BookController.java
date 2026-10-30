package vn.bookstore.controller.web;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Book;
import vn.bookstore.service.BookService;
import vn.bookstore.service.CategoryService;
import vn.bookstore.service.StoreService;

/**
 * Trang công khai hiển thị TẤT CẢ sách, phân trang 6 sách / trang, có thể
 * lọc theo từ khóa / danh mục / chi nhánh. URL: /books?page=0
 */
@Controller
@RequiredArgsConstructor
public class BookController {

	private static final int PAGE_SIZE = 6;

	private final BookService bookService;
	private final CategoryService categoryService;
	private final StoreService storeService;

	@GetMapping("/books")
	public String list(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(required = false) Integer storeId,
			Model model) {

		page = Math.max(0, page);
		Page<Book> bookPage = bookService.findAll(page, PAGE_SIZE, keyword, categoryId, storeId);

		model.addAttribute("bookPage", bookPage);
		model.addAttribute("books", bookPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", bookPage.getTotalPages());
		model.addAttribute("keyword", keyword);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("storeId", storeId);
		model.addAttribute("categories", categoryService.findActive());
		model.addAttribute("stores", storeService.findActive());
		return "store/book-list";
	}

	@GetMapping("/books/{id}")
	public String detail(@PathVariable Integer id, Model model) {
		Book book = bookService.findById(id);
		model.addAttribute("book", book);
		return "store/book-detail";
	}
}
