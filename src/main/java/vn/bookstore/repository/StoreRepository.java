package vn.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.bookstore.entity.Store;

public interface StoreRepository extends JpaRepository<Store, Integer> {
	List<Store> findByStatus(int status);
	List<Store> findByStoreNameContainingIgnoreCase(String keyword);
}
