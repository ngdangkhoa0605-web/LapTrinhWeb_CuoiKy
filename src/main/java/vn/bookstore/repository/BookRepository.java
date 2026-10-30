
package vn.bookstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import vn.bookstore.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {

	Page<Book> findByStatus(int status, Pageable pageable);

	Page<Book> findByStatusAndTitleContainingIgnoreCase(int status, String keyword, Pageable pageable);

	Page<Book> findByStatusAndCategory_CategoryId(int status, Integer categoryId, Pageable pageable);

	Page<Book> findByStatusAndStore_StoreId(int status, Integer storeId, Pageable pageable);

	@Query("""
			SELECT b
			FROM Book b
			WHERE b.status = 1

			AND (
			    :keyword IS NULL
			    OR :keyword = ''
			    OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			)

			AND (
			    :categoryId IS NULL
			    OR b.category.categoryId = :categoryId
			)

			AND (
			    :storeId IS NULL
			    OR b.store.storeId = :storeId
			)

			ORDER BY b.bookId DESC
			""")
	Page<Book> searchBooks(@Param("keyword") String keyword, @Param("categoryId") Integer categoryId,
			@Param("storeId") Integer storeId, Pageable pageable);

	@Query("""
			SELECT b
			FROM Book b
			WHERE b.status = 1
			ORDER BY b.createdDate DESC, b.bookId DESC
			""")
	Page<Book> findLatestBooks(Pageable pageable);
}
