package vn.bookstore.controller.api;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import vn.bookstore.dto.ApiResponse;
import vn.bookstore.entity.Book;
import vn.bookstore.service.BookService;
import vn.bookstore.service.CategoryService;
import vn.bookstore.service.StoreService;

/**
 * API công khai (KHÔNG cần JWT - xem SecurityConfig.apiFilterChain), phục
 * vụ cho việc test bằng Postman hoặc 1 ứng dụng mobile/SPA trong tương lai.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookApiController {

	private static final int PAGE_SIZE = 6;

	private final BookService bookService;
	private final CategoryService categoryService;
	private final StoreService storeService;

	@GetMapping("/books")
	public ApiResponse<Page<Book>> list(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) Integer categoryId,
			@RequestParam(required = false) Integer storeId) {
		Page<Book> books = bookService.findAll(page, PAGE_SIZE, keyword, categoryId, storeId);
		return ApiResponse.ok(books);
	}

	@GetMapping("/books/latest")
	public ApiResponse<?> latest(@RequestParam(defaultValue = "10") int limit) {
		return ApiResponse.ok(bookService.findLatest(limit).getContent());
	}

	@GetMapping("/books/{id}")
	public ApiResponse<Book> detail(@PathVariable Integer id) {
		return ApiResponse.ok(bookService.findById(id));
	}

	@GetMapping("/categories")
	public ApiResponse<?> categories() {
		return ApiResponse.ok(categoryService.findActive());
	}

	@GetMapping("/stores")
	public ApiResponse<?> stores() {
		return ApiResponse.ok(storeService.findActive());
	}
}
