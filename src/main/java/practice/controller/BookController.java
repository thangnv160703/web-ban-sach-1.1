package practice.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import practice.controller.BookController.FileUploadUtil;
import practice.model.Book;
import practice.model.Comment;
import practice.model.Genre;
import practice.serviceImpl.BookServiceImpl;
import practice.serviceImpl.CommentServiceImpl;
import practice.serviceImpl.GenreServiceImpl;

@Controller
@RequestMapping("/book")
public class BookController {

	@Autowired
	private BookServiceImpl bookServiceImpl;

	@Autowired
	private GenreServiceImpl genreServiceImpl;

	@Autowired
	public CommentServiceImpl commentServiceImpl;

//	@GetMapping
//	public String showAllBook(@RequestParam(required = false, value = "page") Integer page, Model model) {
//
//		List<Book> book_list = bookServiceImpl.findAll();
//
//		int len = book_list.size() / 10;
//		if (book_list.size() % 10 != 0) {
//			len += 1;
//		}
//		List<Integer> pagination = new ArrayList<>();
//		for (int i = 1; i <= len; ++i) {
//			pagination.add(i);
//		}
//		model.addAttribute("pagination", pagination);
//		int currentPage = 1;
//		if (page != null) {
//			if (page > len) {
//				return "redirect:/book?page=" + len;
//			}
//			currentPage = page;
//		}
//		model.addAttribute("current", currentPage);
//
//		List<Book> viewedBooks = new ArrayList<>();
//		int start = (currentPage - 1) * 10;
//		int end = (currentPage * 10);
//		for (int i = start; i < end && i < book_list.size(); ++i) {
//			Book book = book_list.get(i);
//			book.setGenres(null);
//			viewedBooks.add(book);
//		}
//		model.addAttribute("book_list", viewedBooks);
//
//		List<Genre> genre_list = genreServiceImpl.findAll();
//		model.addAttribute("genre_list", genre_list);
//
//		return "admin/manage-books";
//	}

	@GetMapping
	public String searchBooks(@RequestParam(defaultValue = "", name = "authorName") String authorName,
			@RequestParam(defaultValue = "", name = "bookName") String bookName,
			@RequestParam(required = false, name = "chosen_genres") List<Long> genre_ids,
			@RequestParam(required = false, value = "page") Integer page, Model model) {

		List<Book> book_list;
		if (genre_ids == null || genre_ids.size() == 0) {
			book_list = bookServiceImpl.findByNameAuthor(bookName, authorName);
		} else {
			List<Genre> genre_list = genreServiceImpl.findAllById(genre_ids);
			book_list = bookServiceImpl.findByNameAuthorGenres(bookName, authorName, genre_list);
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
			if (page > len) {
				return "redirect:/book?page=" + len;
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

		return "admin/manage-books";
	}

	@GetMapping("/add")
	public String addNewBook(Model model) {
		Book book = new Book();
		book.setImage("image");

		List<Genre> genre_list = genreServiceImpl.findAll();
		model.addAttribute("genre_list", genre_list);

		model.addAttribute("book", book);
		return "admin/add-book";
	}

	@PostMapping("/add")
	public String addBookResolve(
			@ModelAttribute("book") Book book, 
			@RequestParam("photo") MultipartFile multipartFile,
			@RequestParam("summary") String note, 
			@RequestParam("forSale") boolean forSale,
			@RequestParam(required = false, name = "chosen_genres") List<Long> genre_ids) throws IOException {

		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		book.setImage(fileName);

		book.setForSale(forSale);

		if (genre_ids != null) {
			List<Genre> genres = genreServiceImpl.findAllById(genre_ids);
			book.setGenres(genres);
		}

		Book savedBook = bookServiceImpl.save(book);
		String uploadDir = "books/" + savedBook.getId();
		FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

		String filepath = "books/" + savedBook.getId() + "/" + savedBook.getId() + ".txt";
		FileUploadUtil.saveNote(note, filepath);

		return "redirect:/book";
	}

	@GetMapping("/detail")
	public String viewBookDetail(@RequestParam(name = "id") Long id,
			@RequestParam(required = false, name = "page") Integer page, Model model) {
//		Get book
		Book book = bookServiceImpl.findById(id);

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
				return "redirect:/book";
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

		return "admin/view-book";
	}

	@GetMapping("/edit/{id}")
	public String editBook(@PathVariable(name = "id") Long id, Model model) {
		Book book = bookServiceImpl.findById(id);
		if (book == null) {
			return "redirect:/book";
		}

		String filepath = "books/" + book.getId() + "/" + book.getId() + ".txt";
		String note = FileUploadUtil.readNote(filepath);

		Note noteObject = new Note();
		noteObject.setContent(note);

		List<Genre> genre_list = genreServiceImpl.findAll();
		model.addAttribute("genre_list", genre_list);

		model.addAttribute("note", noteObject);
		model.addAttribute("book", book);
		return "admin/edit-book";
	}

	@PostMapping("/edit")
	public String editBookResolve(@ModelAttribute("book") Book book, @ModelAttribute("note") Note noteObject,
			@RequestParam("photo") MultipartFile multipartFile, @RequestParam("forSale") boolean forSale,
			@RequestParam(required = false, name = "chosen_genres") List<Long> genre_ids) throws IOException {

		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename()).strip();

		if (!fileName.equals("")) {
			book.setImage(fileName);
			String uploadDir = "books/" + book.getId();
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}

		book.setForSale(forSale);

		if (genre_ids != null) {
			List<Genre> genres = genreServiceImpl.findAllById(genre_ids);
			book.setGenres(genres);
		}

		Book savedBook = bookServiceImpl.save(book);

		String note = noteObject.getContent();

//		System.out.println(note);

		String filepath = "books/" + savedBook.getId() + "/" + savedBook.getId() + ".txt";
		FileUploadUtil.saveNote(note, filepath);
//		System.out.println("Note: " + note);

		return "redirect:/book/detail?id=" + savedBook.getId();
	}

	@GetMapping("/allowsale")
	public String disallowSale(@RequestParam(name = "id") Long id, 
			@RequestParam(defaultValue = "1", name = "page") Integer page,
			@RequestParam(defaultValue = "", name = "authorName") String authorName,
			@RequestParam(defaultValue = "", name = "bookName") String bookName,
			@RequestParam(required = false, name = "chosen_genres") List<Long> genre_ids,
			@RequestParam(name = "sale") Boolean allow) {
		Book book = bookServiceImpl.findById(id);
		book.setForSale(allow);
		bookServiceImpl.save(book);

		String url = "redirect:/book?page=" + page + "&bookName=" + bookName + "&authorName=" + authorName;
		if (genre_ids != null) {
			for (Long genre : genre_ids) {
				url += "&chosen_genres=" + genre;
			}
		}

		return url;
	}

	@GetMapping("/comment/delete")
	public String deleteComment(@RequestParam(name = "bookId") Long bookId,
			@RequestParam(name = "commentId") Long commentId) {
		commentServiceImpl.delete(commentId);
		return "redirect:/book/detail?id=" + bookId;
	}

	@Data
	private class Note {
		private String content;
	}

// Used for saving files and images.
	public class FileUploadUtil {

		public static void saveNote(String note, String filepath) {
			Path path = Paths.get(filepath);
//			System.out.println("Path: " + path.toString());

			try {
				FileWriter myWriter = new FileWriter(path.toString());
				myWriter.write(note);
				myWriter.close();
				System.out.println("Successfully wrote to the file.");
			} catch (IOException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}
		}

		public static String readNote(String filepath) {
			Path path = Paths.get(filepath);
			String data = "";
			try {
				File file = new File(path.toString());
				Scanner scanner = new Scanner(file);
				while (scanner.hasNextLine()) {
					data += scanner.nextLine() + "\n";
				}
				scanner.close();
			} catch (FileNotFoundException e) {
				System.out.println("An error occurred.");
				e.printStackTrace();
			}

			return data;
		}

		public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
			Path uploadPath = Paths.get(uploadDir);

			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

			try (InputStream inputStream = multipartFile.getInputStream()) {
				Path filePath = uploadPath.resolve(fileName);
				Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException ioe) {
				throw new IOException("Could not save image file: " + fileName, ioe);
			}
		}
	}
}
