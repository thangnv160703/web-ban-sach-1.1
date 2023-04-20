package practice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import practice.model.Book;

@Repository(value = "bookRepo")
public interface BookRepository extends CrudRepository<Book, Long> {

	List<Book> findByNameContainingIgnoreCaseAndAuthorContainingIgnoreCase(String name, String author);
	
	List<Book> findByIsForSale(boolean isForSale);
	
}
