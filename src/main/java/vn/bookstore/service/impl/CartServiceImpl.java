package vn.bookstore.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Book;
import vn.bookstore.entity.Cart;
import vn.bookstore.entity.CartItem;
import vn.bookstore.entity.User;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.repository.BookRepository;
import vn.bookstore.repository.CartItemRepository;
import vn.bookstore.repository.CartRepository;
import vn.bookstore.service.CartService;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final BookRepository bookRepository;

	@Override
	@Transactional
	public Cart getOrCreateCart(User user) {
		return cartRepository.findByUser_UserId(user.getUserId())
				.orElseGet(() -> cartRepository.save(new Cart(user)));
	}
	@Override
	@Transactional
	public Cart addItem(User user, Integer bookId, int quantity) {

	    if (quantity <= 0) {
	        throw new BusinessException(
	                "Số lượng sản phẩm phải lớn hơn 0"
	        );
	    }


	    Book book = bookRepository.findById(bookId)
	            .orElseThrow(() ->
	                    new BusinessException(
	                            "Không tìm thấy sách có id = " + bookId
	                    )
	            );

	    if (!book.isInStock()) {
	        throw new BusinessException(
	                "Sách \"" + book.getTitle() + "\" hiện đã hết hàng"
	        );
	    }

	    if (book.getQuantity() < quantity) {
	        throw new BusinessException(
	                "Sách \"" + book.getTitle()
	                        + "\" chỉ còn "
	                        + book.getQuantity()
	                        + " cuốn trong kho"
	        );
	    }

	    Cart cart = getOrCreateCart(user);


	    CartItem item =
	            cartItemRepository
	                    .findByCart_CartIdAndBook_BookId(
	                            cart.getCartId(),
	                            bookId
	                    )
	                    .orElse(null);


	    if (item == null) {

	        item = new CartItem(
	                cart,
	                book,
	                quantity
	        );

	    }
	    else {

	        int oldQuantity = item.getQuantity();

	        int newQuantity = oldQuantity + quantity;

	        if (newQuantity > book.getQuantity()) {

	            throw new BusinessException(
	                    "Sách \"" + book.getTitle()
	                            + "\" chỉ còn "
	                            + book.getQuantity()
	                            + " cuốn trong kho. "
	                            + "Trong giỏ hiện có "
	                            + oldQuantity
	                            + " cuốn."
	            );
	        }

	        item.setQuantity(newQuantity);
	    }

	    cartItemRepository.save(item);

	    return cartRepository
	            .findById(cart.getCartId())
	            .orElse(cart);
	}


	@Override
	@Transactional
	public Cart updateItemQuantity(User user, Integer cartItemId, int quantity) {
		CartItem item = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new BusinessException("Không tìm thấy sản phẩm trong giỏ hàng"));

		if (!item.getCart().getUser().getUserId().equals(user.getUserId())) {
			throw new BusinessException("Bạn không có quyền chỉnh sửa giỏ hàng này");
		}

		if (quantity <= 0) {
			cartItemRepository.delete(item);
		} else {
			if (item.getBook().getQuantity() < quantity) {
				throw new BusinessException("Số lượng vượt quá tồn kho hiện có (" + item.getBook().getQuantity() + ")");
			}
			item.setQuantity(quantity);
			cartItemRepository.save(item);
		}
		return cartRepository.findById(item.getCart().getCartId()).orElse(null);
	}

	@Override
	@Transactional
	public Cart removeItem(User user, Integer cartItemId) {
		return updateItemQuantity(user, cartItemId, 0);
	}

	@Override
	@Transactional
	public void clearCart(User user) {
		Cart cart = getOrCreateCart(user);
		cartItemRepository.deleteAll(cart.getItems());
	}
}
