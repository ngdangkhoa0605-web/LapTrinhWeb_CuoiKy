package vn.bookstore.service;

import vn.bookstore.entity.Cart;
import vn.bookstore.entity.User;

public interface CartService {
	Cart getOrCreateCart(User user);
	Cart addItem(User user, Integer bookId, int quantity);
	Cart updateItemQuantity(User user, Integer cartItemId, int quantity);
	Cart removeItem(User user, Integer cartItemId);
	void clearCart(User user);
}
