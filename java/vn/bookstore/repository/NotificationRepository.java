package vn.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.bookstore.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {
	List<Notification> findByStore_StoreIdOrderByCreatedDateDesc(Integer storeId);
	long countByStore_StoreIdAndIsReadFalse(Integer storeId);
}
