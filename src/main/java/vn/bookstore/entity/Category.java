package vn.bookstore.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Integer categoryId;

	@NotEmpty(message = "Tên danh mục không được để rỗng")
	@Column(name = "category_name", nullable = false, length = 255)
	private String categoryName;

	@Column(name = "description", length = 1000)
	private String description;

	@Column(name = "image", length = 500)
	private String image;

	/** 1 = hiển thị, 0 = ẩn */
	@Column(name = "status")
	private int status = 1;

	@JsonIgnore
	@OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private List<Book> books = new ArrayList<>();

	public Category(String categoryName, String description, String image, int status) {
		this.categoryName = categoryName;
		this.description = description;
		this.image = image;
		this.status = status;
	}
}
