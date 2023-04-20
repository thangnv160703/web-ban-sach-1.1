package practice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import practice.model.DonHang;
import practice.model.User;

@Repository(value = "orderRepo")
public interface OrderRepository extends CrudRepository<DonHang, Long>{
	
	List<DonHang> findByUser(User user);
	
	List<DonHang> findByIsDelivered(boolean isDelivered);
	
	List<DonHang> findByUserAndIsDelivered(User user, boolean isDelivered);
	
}
