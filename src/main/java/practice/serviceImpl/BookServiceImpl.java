package practice.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.model.Book;
import practice.model.Genre;
import practice.repository.BookRepository;

@Service
public class BookServiceImpl{

	@Autowired
	private BookRepository bookRepo;
	
	public List<Book> findAll(){
		List<Book> books = (List<Book>) bookRepo.findAll();
		return books;
	}
	
	public List<Book> findAllById(List<Long> ids){
		List<Book> books = (List<Book>) bookRepo.findAllById(ids);
		return books;
	}
	
	public List<Book> findByNameAuthor(String name, String author){
		List<Book> books = bookRepo.findByNameContainingIgnoreCaseAndAuthorContainingIgnoreCase(name, author);
		return books;
	}
	
	public List<Book> findByNameAuthorGenres(String name, String author, List<Genre> genres){
		List<Book> books = bookRepo.findByNameContainingIgnoreCaseAndAuthorContainingIgnoreCase(name, author);
		List<Book> acceptedBooks = new ArrayList<>();
		for(Book book: books) {
			if(book.hasGenres(genres)) {
				acceptedBooks.add(book);
			}
		}
		return acceptedBooks;
	}
	
	public Book findById(Long id) {
		Book book = bookRepo.findById(id).orElse(null);
		return book;
	}
	
	public Book save(Book book) {
		book.setNote(null);
		
		Book savedBook = bookRepo.save(book);
		
		return savedBook;
	}
	
	public void delete(Book book) {
		bookRepo.delete(book);
	}
	
	public List<Book> findBooksForSale(){
		List<Book> books = bookRepo.findByIsForSale(true);
		return books;
	}
	
	public List<Book> findBooksForSaleByNamAuthorGenres(String name, String author, List<Genre> genres){
		List<Book> book_list = findByNameAuthorGenres(name, author, genres);
		List<Book> books = new ArrayList<>();
		for(Book book: book_list) {
			if(book.isForSale()) {
				books.add(book);
			}
		}
		return books;
	}

}
