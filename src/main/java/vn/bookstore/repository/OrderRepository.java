package vn.bookstore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.bookstore.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
	List<Order> findByCustomer_UserIdOrderByOrderDateDesc(Integer customerId);
	Page<Order> findByStore_StoreIdOrderByOrderDateDesc(Integer storeId, Pageable pageable);
	Page<Order> findByStore_StoreIdAndStatusOrderByOrderDateDesc(Integer storeId, String status, Pageable pageable);
	Page<Order> findAllByOrderByOrderDateDesc(Pageable pageable);
	long countByStore_StoreIdAndStatus(Integer storeId, String status);
	@Query("""
		    SELECT o
		    FROM Order o
		    WHERE (
		        :status IS NULL
		        OR :status = ''
		        OR o.status = :status
		    )
		    ORDER BY o.orderDate DESC
		    """)
		Page<Order> findForAdmin(
		        @Param("status") String status,
		        Pageable pageable
		);
}
