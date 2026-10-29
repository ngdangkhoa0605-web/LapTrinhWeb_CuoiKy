package vn.bookstore.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.RequiredArgsConstructor;

/**
 * Upload ảnh (Category/Book/Store) lên Cloudinary thay vì lưu ổ đĩa local -
 * phù hợp khi deploy lên các nền tảng cloud (Render/Railway/Heroku...) vì
 * ổ đĩa container không bền vững giữa các lần deploy.
 */
@Service
@RequiredArgsConstructor
public class CloudinaryService {

	private final Cloudinary cloudinary;

	/**
	 * @param file   file ảnh người dùng upload
	 * @param folder thư mục logic trên Cloudinary (vd: "books", "categories", "stores")
	 * @return URL công khai (secure_url) của ảnh vừa upload, hoặc null nếu file rỗng
	 */
	@SuppressWarnings("unchecked")
	public String uploadImage(MultipartFile file, String folder) throws IOException {
		if (file == null || file.isEmpty()) {
			return null;
		}
		Map<String, Object> options = ObjectUtils.asMap(
				"folder", "bookstore-chain/" + folder,
				"resource_type", "image");
		Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(), options);
		return (String) result.get("secure_url");
	}

	/** Xóa ảnh trên Cloudinary theo publicId (tùy chọn, dùng khi cần dọn ảnh cũ). */
	public void deleteImage(String publicId) {
		try {
			cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
		} catch (IOException e) {
			// Không cho phép lỗi xóa ảnh cũ làm gián đoạn nghiệp vụ chính (vd: cập nhật sách)
			e.printStackTrace();
		}
	}
}
