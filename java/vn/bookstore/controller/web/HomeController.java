package vn.bookstore.controller.web;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Book;
import vn.bookstore.service.BookService;
import vn.bookstore.service.CategoryService;
import vn.bookstore.service.StoreService;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final BookService bookService;
	private final CategoryService categoryService;
	private final StoreService storeService;

	@GetMapping({ "/", "/home" })
	public String home(Model model) {
		Page<Book> latest = bookService.findLatest(10);
		model.addAttribute("latestBooks", latest.getContent());
		model.addAttribute("categories", categoryService.findActive());
		model.addAttribute("stores", storeService.findActive());
		return "store/home";
	}
}
