package vn.bookstore.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Book;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.repository.BookRepository;
import vn.bookstore.service.BookService;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;

	@Override
	public Book save(Book book) {
		return bookRepository.save(book);
	}

	@Override
	public Book findById(Integer id) {
		return bookRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Không tìm thấy sách có id = " + id));
	}

	@Override
	public void delete(Integer id) {
		Book book = findById(id);
		bookRepository.delete(book);
	}

	@Override
	public Page<Book> findLatest(int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return bookRepository.findLatestBooks(pageable);
	}

	@Override
	public Page<Book> findAll(int page, int size, String keyword, Integer categoryId, Integer storeId) {
		Pageable pageable = PageRequest.of(Math.max(page, 0), size, Sort.by(Sort.Direction.DESC, "bookId"));

		if (keyword != null && !keyword.trim().isEmpty()) {
			return bookRepository.findByStatusAndTitleContainingIgnoreCase(1, keyword.trim(), pageable);
		}
		if (categoryId != null) {
			return bookRepository.findByStatusAndCategory_CategoryId(1, categoryId, pageable);
		}
		if (storeId != null) {
			return bookRepository.findByStatusAndStore_StoreId(1, storeId, pageable);
		}
		return bookRepository.findByStatus(1, pageable);
	}
}
