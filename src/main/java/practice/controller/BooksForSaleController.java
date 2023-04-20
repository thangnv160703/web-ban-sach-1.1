package practice.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import practice.controller.BookController.FileUploadUtil;
import practice.model.Book;
import practice.model.Comment;
import practice.model.Genre;
import practice.model.User;
import practice.serviceImpl.BookServiceImpl;
import practice.serviceImpl.CommentServiceImpl;
import practice.serviceImpl.GenreServiceImpl;
import practice.serviceImpl.UserServiceImpl;

@Controller
@RequestMapping("/booksforsale")
public class BooksForSaleController {

	@Autowired
	private BookServiceImpl bookServiceImpl;

	@Autowired
	private GenreServiceImpl genreServiceImpl;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private CommentServiceImpl commentServiceImpl;

	@GetMapping
	public String showAllBook(@RequestParam(required = false, value = "page") Integer page, 
			Model model, Principal principal) {

		List<Book> book_list = bookServiceImpl.findBooksForSale();

		int len = book_list.size() / 10;
		if (book_list.size() % 10 != 0) {
			len += 1;
		}

		List<Integer> pagination = new ArrayList<>();
		for (int i = 1; i <= len; ++i) {
			pagination.add(i);
		}
		model.addAttribute("pagination", pagination);

		int currentPage = 1;
		if (page != null) {
			if (page > len) {
				return "redirect:/booksforsale?page=" + len;
			}
			currentPage = page;
		}

		model.addAttribute("current", currentPage);

		List<Book> viewedBooks = new ArrayList<>();
		int start = (currentPage - 1) * 10;
		int end = (currentPage * 10);
		for (int i = start; i < end && i < book_list.size(); ++i) {
			Book book = book_list.get(i);
			book.setGenres(null);
			viewedBooks.add(book);
		}

		model.addAttribute("book_list", viewedBooks);

		List<Genre> genre_list = genreServiceImpl.findAll();
		model.addAttribute("genre_list", genre_list);

//		Check role of user 
		boolean isUser = false;
		if(principal != null) {
			isUser = true;
		}
		model.addAttribute("isUser", isUser);

		return "general/view-books-for-sale";
	}

	@GetMapping("/search")
	public String searchBooks(
			@RequestParam(defaultValue = "", name = "authorName") String authorName,
			@RequestParam(defaultValue = "", name = "bookName") String bookName,
			@RequestParam(required = false, name = "chosen_genres") List<Long> genre_ids,
			@RequestParam(required = false, value = "page") Integer page, 
			Model model, Principal principal) {

		List<Book> books;
		List<Book> book_list = new ArrayList<Book>();
		if (genre_ids == null || genre_ids.size() == 0) {
			books = bookServiceImpl.findByNameAuthor(bookName, authorName);
		} else {
			List<Genre> genre_list = genreServiceImpl.findAllById(genre_ids);
			books = bookServiceImpl.findByNameAuthorGenres(bookName, authorName, genre_list);
		}
		for (Book book : books) {
			if (book.isForSale()) {
				book_list.add(book);
			}
		}

		int len = book_list.size() / 10;
		if (book_list.size() % 10 != 0) {
			len += 1;
		}
		List<Integer> pagination = new ArrayList<>();
		for (int i = 1; i <= len; ++i) {
			pagination.add(i);
		}
		model.addAttribute("pagination", pagination);
		int currentPage = 1;
		if (page != null) {
			if (page > len || page<1) {
				return "redirect:/booksforsale?page=" + 1;
			}
			currentPage = page;
		}
		model.addAttribute("current", currentPage);

		List<Book> viewedBooks = new ArrayList<>();
		int start = (currentPage - 1) * 10;
		int end = (currentPage * 10);
		for (int i = start; i < end && i < book_list.size(); ++i) {
			Book book = book_list.get(i);
			book.setGenres(null);
			viewedBooks.add(book);
		}
		model.addAttribute("book_list", viewedBooks);

		List<Genre> genres = genreServiceImpl.findAll();
		model.addAttribute("genre_list", genres);

		model.addAttribute("searchedName", bookName);
		model.addAttribute("searchAuthor", authorName);
		model.addAttribute("searchedGenres", genre_ids);
		
		boolean isUser = false;
		if(principal != null) {
			isUser = true;
		}
		model.addAttribute("isUser", isUser);

		return "general/view-books-for-sale";
	}

	@GetMapping("/detail")
	public String viewBookDetail(@RequestParam(name = "id") Long id,
			@RequestParam(required = false, name = "page") Integer page, 
			Model model, Principal principal) {
		boolean isUser = true;
		if(principal == null) {
			isUser = false;
		}
		model.addAttribute("isUser", isUser);
		
//		Get book
		Book book = bookServiceImpl.findById(id);
		if (book == null || !book.isForSale()) {
			return "redirect:/booksforsale";
		}
		String filepath = "books/" + book.getId() + "/" + book.getId() + ".txt";
		String note = FileUploadUtil.readNote(filepath);
		book.setNote(note);

//		Get genres
		String genres = "";
		for (Genre g : book.getGenres()) {
			genres += g.getName() + ", ";
		}
		if (genres.length() > 2) {
			genres = genres.substring(0, genres.length() - 2);
		}
		book.setGenres(null);
		model.addAttribute("book", book);
		model.addAttribute("genres", genres);

//		Get comments
		List<Comment> comments = commentServiceImpl.findByBook(book);
		Collections.sort(comments);
		int len = comments.size() / 10;
		if (comments.size() % 10 != 0) {
			len += 1;
		}
		List<Integer> pagination = new ArrayList<>();
		for (int i = 1; i <= len; ++i) {
			pagination.add(i);
		}
		model.addAttribute("pagination", pagination);
		int currentPage = 1;
		if (page != null) {
			if (page > len || page < 1) {
				return "redirect:/booksforsale";
			}
			currentPage = page;
		}
		model.addAttribute("current", currentPage);

//		Doc tu file
		List<Comment> viewedComments = new ArrayList<>();
		int start = (currentPage - 1) * 10;
		int end = (currentPage * 10);
		for (int i = start; i < end && i < comments.size(); ++i) {
			Comment com = comments.get(i);
			String path = "books/" + book.getId() + "/comment_" + com.getId() + ".txt";
			String content = FileUploadUtil.readNote(path);
			com.setComment(content);
			viewedComments.add(com);
		}
		model.addAttribute("commentList", viewedComments);

		return "general/view-book-for-sale-detail";
	}

	@PostMapping("/comment/add")
	public String addComments(@RequestParam(name = "userComment") String userComment,
			@RequestParam(name = "bookId") Long bookId,
			Principal principal) {
//		GEt user
		String username = principal.getName();
		if(username == null) {
			return "redirect:/";
		}
		User user = userServiceImpl.findByUsername(username);
//		Change later

		Book book = bookServiceImpl.findById(bookId);
		if(!book.isForSale()) {
			return "redirect:/booksforsale";
		}

		Comment comment = new Comment();
		comment.setBook(book);
		comment.setUser(user);

		Comment savedComment = commentServiceImpl.save(comment);
		String name = "comment_" + savedComment.getId();

		userComment = userComment.trim();

		String filepath = "books/" + bookId + "/" + name + ".txt";
		FileUploadUtil.saveNote(userComment, filepath);

		return "redirect:/booksforsale/detail?id=" + bookId.toString();
	}

}
