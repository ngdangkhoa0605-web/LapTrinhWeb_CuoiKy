package vn.bookstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.bookstore.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	List<CartItem> findByCart_CartId(Integer cartId);
	Optional<CartItem> findByCart_CartIdAndBook_BookId(Integer cartId, Integer bookId);
}
