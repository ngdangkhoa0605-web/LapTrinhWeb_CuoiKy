package vn.bookstore.service;

import java.util.List;

import vn.bookstore.entity.Category;

public interface CategoryService {
	Category save(Category category);
	Category findById(Integer id);
	List<Category> findAll();
	List<Category> findActive();
	List<Category> search(String keyword);
	void delete(Integer id);
}
