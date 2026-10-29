package vn.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.bookstore.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	List<Category> findByStatus(int status);
	List<Category> findByCategoryNameContainingIgnoreCase(String keyword);
}
