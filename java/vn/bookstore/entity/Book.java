package vn.bookstore.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 1 đầu sách cũ đang được bán tại 1 chi nhánh cụ thể (Store) và thuộc 1
 * danh mục (Category). Vì là sách cũ nên có thêm trường "conditionGrade"
 * (tình trạng sách: NEW / LIKE_NEW / GOOD / FAIR).
 */
@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "book_id")
	private Integer bookId;

	@NotEmpty(message = "Tên sách không được để rỗng")
	@Column(name = "title", nullable = false, length = 500)
	private String title;

	@Column(name = "author", length = 255)
	private String author;

	@Column(name = "publisher", length = 255)
	private String publisher;

	@Column(name = "isbn", length = 50)
	private String isbn;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	/** URL ảnh trên Cloudinary */
	@Column(name = "image", length = 500)
	private String image;

	@NotNull(message = "Giá bán không được để trống")
	@Column(name = "price", nullable = false, precision = 12, scale = 2)
	private BigDecimal price;

	@Column(name = "original_price", precision = 12, scale = 2)
	private BigDecimal originalPrice;

	@Column(name = "quantity")
	private int quantity;

	/** NEW, LIKE_NEW, GOOD, FAIR - tình trạng sách cũ */
	@Column(name = "condition_grade", length = 20)
	private String conditionGrade = "GOOD";

	/** 1 = đang bán, 0 = ngừng bán */
	@Column(name = "status")
	private int status = 1;

	@Column(name = "created_date")
	private LocalDateTime createdDate = LocalDateTime.now();

	@NotNull(message = "Vui lòng chọn danh mục")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@NotNull(message = "Vui lòng chọn chi nhánh")
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	public boolean isInStock() {
		return quantity > 0 && status == 1;
	}
}
