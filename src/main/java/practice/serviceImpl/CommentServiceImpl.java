package practice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.model.Book;
import practice.model.Comment;
import practice.repository.CommentRepository;

@Service
public class CommentServiceImpl {

	@Autowired
	private CommentRepository commentRepo;
	
	public List<Comment> findByBook(Book book){
		List<Comment> comments = commentRepo.findByBook(book);
		return comments;
	}
	
	public Comment save(Comment comment) {
		comment.setComment(null);
		Comment savedComment = commentRepo.save(comment);
		return savedComment;
	}
	
	public void delete(Long id) {
		commentRepo.deleteById(id);;
	}
}
