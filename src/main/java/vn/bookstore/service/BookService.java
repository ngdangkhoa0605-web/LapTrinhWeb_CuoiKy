package vn.bookstore.service;

import org.springframework.data.domain.Page;

import vn.bookstore.entity.Book;

public interface BookService {
	Book save(Book book);
	Book findById(Integer id);
	void delete(Integer id);

	/** Trang chủ: 10 sách mới nhất. */
	Page<Book> findLatest(int limit);

	/** Trang /books: tất cả sách, phân trang 6 sách/trang, có thể lọc theo từ khóa/danh mục/chi nhánh. */
	Page<Book> findAll(int page, int size, String keyword, Integer categoryId, Integer storeId);
}
