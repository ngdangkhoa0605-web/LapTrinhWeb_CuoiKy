package vn.bookstore.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import vn.bookstore.entity.Category;
import vn.bookstore.exception.BusinessException;
import vn.bookstore.repository.CategoryRepository;
import vn.bookstore.service.CategoryService;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public Category findById(Integer id) {
		return categoryRepository.findById(id)
				.orElseThrow(() -> new BusinessException("Không tìm thấy danh mục có id = " + id));
	}

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public List<Category> findActive() {
		return categoryRepository.findByStatus(1);
	}

	@Override
	public List<Category> search(String keyword) {
		return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword);
	}

	@Override
	public void delete(Integer id) {
		Category category = findById(id);
		if (!category.getBooks().isEmpty()) {
			throw new BusinessException(
					"Không thể xóa danh mục đang có " + category.getBooks().size() + " đầu sách!");
		}
		categoryRepository.delete(category);
	}
}
