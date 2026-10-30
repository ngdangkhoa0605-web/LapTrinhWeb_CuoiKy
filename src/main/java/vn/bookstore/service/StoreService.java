package vn.bookstore.service;

import java.util.List;

import vn.bookstore.entity.Store;

public interface StoreService {
	Store save(Store store);
	Store findById(Integer id);
	List<Store> findAll();
	List<Store> findActive();
	void delete(Integer id);
}
