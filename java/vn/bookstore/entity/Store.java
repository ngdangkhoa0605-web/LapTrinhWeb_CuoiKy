package vn.bookstore.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 1 chi nhánh trong chuỗi cửa hàng sách cũ. 1 Store có nhiều Book (tồn kho
 * riêng theo chi nhánh) và nhiều User (nhân viên/quản lý làm việc tại đó).
 */
@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
public class Store implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "store_id")
	private Integer storeId;

	@NotEmpty(message = "Tên chi nhánh không được để rỗng")
	@Column(name = "store_name", nullable = false, length = 255)
	private String storeName;

	@Column(name = "address", length = 500)
	private String address;

	@Column(name = "phone", length = 20)
	private String phone;

	@Column(name = "image", length = 500)
	private String image;

	/** 1 = đang hoạt động, 0 = ngưng hoạt động */
	@Column(name = "status")
	private int status = 1;

	@Column(name = "created_date")
	private LocalDateTime createdDate = LocalDateTime.now();

	@JsonIgnore
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY)
	private List<Book> books = new ArrayList<>();

	public Store(String storeName, String address, String phone) {
		this.storeName = storeName;
		this.address = address;
		this.phone = phone;
	}
}
