package practice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import practice.model.User;

@Repository(value = "customerRepo")
public interface UserRepository extends CrudRepository<User, Long>{

	User findByUsername(String username);
	
	List<User> findByRole(String role);
	
	List<User> findByUsernameContainingIgnoreCase(String username);
	
	List<User> findByUsernameContainingIgnoreCaseAndNameContainingIgnoreCase(String username, String name);
	
}
