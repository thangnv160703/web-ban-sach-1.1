package practice.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import practice.model.Book;
import practice.model.Comment;

@Repository(value = "commentRepo")
public interface CommentRepository extends CrudRepository<Comment, Long>{

	List<Comment> findByBook(Book book);
	
}
