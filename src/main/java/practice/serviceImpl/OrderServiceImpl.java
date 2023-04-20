package practice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.model.DonHang;
import practice.model.User;
import practice.repository.OrderRepository;

@Service
public class OrderServiceImpl {

	@Autowired
	private OrderRepository orderRepo;
	
	public DonHang findById(Long id) {
		DonHang order = orderRepo.findById(id).orElse(null);
		return order;
	}
	
	public List<DonHang> findByUserDelivered(User user, boolean isDelivered){
		List<DonHang> donHangs = orderRepo.findByUserAndIsDelivered(user, isDelivered);
		return donHangs;
	}
	
	public List<DonHang> findByUser(User user){
		List<DonHang> donHangs = orderRepo.findByUser(user);
		return donHangs;
	}
	
	public List<DonHang> findByIsDeliverd(boolean isDelivered){
		List<DonHang> donHangs = orderRepo.findByIsDelivered(false);
		return donHangs;
	}
	
	public DonHang save(DonHang donHang) {
		DonHang savedOrder = orderRepo.save(donHang);
		return savedOrder;
	}
	
	public void delete(Long id) {
		orderRepo.deleteById(id);;
	}
}
