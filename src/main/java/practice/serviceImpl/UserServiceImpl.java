package practice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.model.User;
import practice.repository.UserRepository;

@Service
public class UserServiceImpl {
	
	@Autowired
	private UserRepository userRepo;
	
	public User findByUsername(String username) {
		User user = userRepo.findByUsername(username);
		return user;
	}
	
	public List<User> findByRole(String role){
		List<User> users = userRepo.findByRole(role);
		return users;
	}
	
	public List<User> findByUsernameIgnoreCase(String username){
		List<User> users = userRepo.findByUsernameContainingIgnoreCase(username);
		return users;
	}
	
	public List<User> findByUsernameName(String username, String name){
		List<User> users = userRepo.findByUsernameContainingIgnoreCaseAndNameContainingIgnoreCase(username, name);
		return users;
	}
	
	public User findById(Long id) {
		User user = userRepo.findById(id).orElse(null);
		return user;
	}
	
	public User save(User user) {
		User savedUser = userRepo.save(user);
		return savedUser;
	}
	
	public void disable(Long id) {
		User user = userRepo.findById(id).orElse(null);
		if(user != null) {
			user.setActive(false);
			userRepo.save(user);
		}
	}
	
	public void enable(Long id) {
		User user = userRepo.findById(id).orElse(null);
		if(user != null) {
			user.setActive(true);
			userRepo.save(user);
		}
	}

}
