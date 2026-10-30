package vn.bookstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.bookstore.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
	List<OrderDetail> findByOrder_OrderId(Integer orderId);
}
