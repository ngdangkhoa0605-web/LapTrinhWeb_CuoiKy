package vn.bookstore.config;

import java.math.BigDecimal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import vn.bookstore.entity.Book;
import vn.bookstore.entity.Category;
import vn.bookstore.entity.Store;
import vn.bookstore.repository.BookRepository;
import vn.bookstore.repository.CategoryRepository;
import vn.bookstore.repository.StoreRepository;

/** Optional catalog for local demonstrations. Never seeds a database with existing books. */
@Component
@Order(2)
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.demo.seed-books", havingValue = "true")
@Slf4j
public class DemoBookSeeder implements CommandLineRunner {
	private final BookRepository books;
	private final StoreRepository stores;
	private final CategoryRepository categories;

	@Override
	public void run(String... args) {
		if (books.count() != 0) {
			log.info("Demo catalog skipped: books already exist.");
			return;
		}
		Store saigon = store("Chi nhánh Quận 1", "123 Nguyễn Huệ, Quận 1, TP.HCM", "0900000001");
		Store hanoi = store("Chi nhánh Hà Nội", "24 Tràng Tiền, Hoàn Kiếm, Hà Nội", "0900000002");
		Store danang = store("Chi nhánh Đà Nẵng", "52 Bạch Đằng, Hải Châu, Đà Nẵng", "0900000003");
		Category literature = category("Văn học");
		Category business = category("Kinh tế");
		Category children = category("Thiếu nhi");
		Category life = category("Kỹ năng sống");
		add("Dế Mèn phiêu lưu ký", "Tô Hoài", "Kim Đồng", literature, saigon, 59000, 85000, 12, "GOOD", "Chuyến phiêu lưu trưởng thành của Dế Mèn, một tác phẩm văn học thiếu nhi quen thuộc.", "literature.svg");
		add("Nhà giả kim", "Paulo Coelho", "Nhã Nam", literature, hanoi, 79000, 115000, 8, "LIKE_NEW", "Hành trình đi tìm kho báu và lắng nghe tiếng gọi của trái tim.", "literature.svg");
		add("Tôi thấy hoa vàng trên cỏ xanh", "Nguyễn Nhật Ánh", "Trẻ", literature, danang, 72000, 110000, 6, "GOOD", "Những ký ức tuổi thơ bình dị và ấm áp ở miền quê Việt Nam.", "literature.svg");
		add("Cho tôi xin một vé đi tuổi thơ", "Nguyễn Nhật Ánh", "Trẻ", literature, saigon, 68000, 100000, 10, "LIKE_NEW", "Một tấm vé trở về thế giới trong trẻo của tuổi thơ.", "literature.svg");
		add("Tuổi trẻ đáng giá bao nhiêu", "Rosie Nguyễn", "Hội Nhà Văn", life, hanoi, 65000, 98000, 9, "GOOD", "Gợi mở cách học, cách đi và cách sống chủ động trong những năm tuổi trẻ.", "life.svg");
		add("Đắc nhân tâm", "Dale Carnegie", "Tổng hợp TP.HCM", life, saigon, 75000, 120000, 15, "LIKE_NEW", "Các nguyên tắc giao tiếp và xây dựng mối quan hệ tích cực.", "life.svg");
		add("7 thói quen hiệu quả", "Stephen R. Covey", "Tổng hợp TP.HCM", life, danang, 99000, 160000, 5, "GOOD", "Phương pháp rèn luyện thói quen để chủ động hơn trong công việc và cuộc sống.", "life.svg");
		add("Cha giàu cha nghèo", "Robert T. Kiyosaki", "Trẻ", business, hanoi, 85000, 125000, 7, "GOOD", "Góc nhìn nhập môn về tài chính cá nhân và tư duy quản lý tiền bạc.", "business.svg");
		add("Quốc gia khởi nghiệp", "Dan Senor, Saul Singer", "Thế Giới", business, saigon, 98000, 145000, 4, "LIKE_NEW", "Khám phá tinh thần đổi mới và hệ sinh thái khởi nghiệp.", "business.svg");
		add("Tư duy nhanh và chậm", "Daniel Kahneman", "Thế Giới", business, danang, 115000, 185000, 6, "GOOD", "Tìm hiểu hai hệ thống tư duy ảnh hưởng đến những quyết định hàng ngày.", "business.svg");
		add("Hoàng tử bé", "Antoine de Saint-Exupéry", "Kim Đồng", children, hanoi, 54000, 80000, 11, "LIKE_NEW", "Câu chuyện về tình bạn, lòng yêu thương và cách nhìn thế giới bằng trái tim.", "children.svg");
		add("Không gia đình", "Hector Malot", "Kim Đồng", children, saigon, 89000, 135000, 5, "GOOD", "Cuộc hành trình của cậu bé Rémi đi tìm mái ấm và những người thân yêu.", "children.svg");
		log.info("Demo catalog created: {} books", books.count());
	}

	private Store store(String name, String address, String phone) {
		return stores.findAll().stream().filter(s -> name.equals(s.getStoreName())).findFirst()
				.orElseGet(() -> stores.save(new Store(name, address, phone)));
	}

	private Category category(String name) {
		return categories.findAll().stream().filter(c -> name.equals(c.getCategoryName())).findFirst()
				.orElseGet(() -> categories.save(new Category(name, "Sách tuyển chọn cho người yêu đọc sách", null, 1)));
	}

	private void add(String title, String author, String publisher, Category category, Store store,
			int price, int originalPrice, int quantity, String condition, String description, String cover) {
		Book book = new Book();
		book.setTitle(title);
		book.setAuthor(author);
		book.setPublisher(publisher);
		book.setDescription(description);
		book.setPrice(BigDecimal.valueOf(price));
		book.setOriginalPrice(BigDecimal.valueOf(originalPrice));
		book.setQuantity(quantity);
		book.setConditionGrade(condition);
		book.setCategory(category);
		book.setStore(store);
		book.setImage("/images/covers/" + cover);
		books.save(book);
	}
}
