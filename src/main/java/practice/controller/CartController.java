package practice.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import practice.model.Book;
import practice.model.Cart;
import practice.model.DonHang;
import practice.model.PurchasedBook;
import practice.model.User;
import practice.serviceImpl.BookServiceImpl;
import practice.serviceImpl.OrderServiceImpl;
import practice.serviceImpl.UserServiceImpl;

@Controller
@RequestMapping("/cart")
@Scope("request")
public class CartController {
	
	@Autowired
	private Cart cart;
	
	@Autowired
	private BookServiceImpl bookServiceImpl;
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	
	@GetMapping
	public String viewCart(Model model) {
		List<Book> books = new ArrayList<>();
		for(Long id: cart.getBookIds().keySet()) {
			Book book = bookServiceImpl.findById(id);
			if(!book.isForSale()) {
				cart.removeBook(id);
				continue;
			}
			books.add(book);
		}
		
		boolean isEmty = true;
		if(books.size() != 0) {
			isEmty=false;
		}
		
		model.addAttribute("book_list", books);
		model.addAttribute("isEmpty", isEmty);
		return "user/view-cart";
	}
	
	@GetMapping("/add/{bookId}")
	public String addBookIntoCart(@PathVariable(name = "bookId") Long bookId) {
		Book book = bookServiceImpl.findById(bookId);
		if(!book.isForSale()) {
			return "redirect:/booksforsale";
		}
		cart.addBooks(bookId);
		return "redirect:/cart";
	}
	
	@GetMapping("/delete/{bookId}")
	public String removeBookFromCart(@PathVariable(name = "bookId") Long bookId) {
		cart.removeBook(bookId);
		return "redirect:/cart";
	}
	
	@PostMapping("/fee")
	public String calculateFee(@RequestParam Map<String, String> map, Model model) {
		int totalFee = 0;
		List<PurchasedBook> purchasedBooks = new ArrayList<>();
		for(Long id: cart.getBookIds().keySet()) {
			Book book = bookServiceImpl.findById(id);
			if(!book.isForSale()) {
				cart.removeBook(id);
				continue;
			}
			int number = Integer.parseInt(map.get(id.toString()));
			cart.orderNumber(id, number);
			PurchasedBook pbook = new PurchasedBook();
			pbook.setBook(book);
			pbook.setNumber(number);
			pbook.setTotalPrice(number * book.getPrice());
			totalFee += pbook.getTotalPrice();
			
			purchasedBooks.add(pbook);
		}
		
		model.addAttribute("totalFee", totalFee);
		model.addAttribute("purchasedBookList", purchasedBooks);
		return "user/view-fee";
	}
	
	@PostMapping("/purchase")
	public String purchaseBooks(Principal principal) {
		int totalFee = 0;
		List<PurchasedBook> purchasedBooks = new ArrayList<>();
		for(Long id: cart.getBookIds().keySet()) {
			Book book = bookServiceImpl.findById(id);
			if(!book.isForSale()) {
				cart.removeBook(id);
				continue;
			}
			PurchasedBook pbook = new PurchasedBook();
			pbook.setBook(book);
			int number = cart.getBookIds().get(id);
			pbook.setNumber(number);
			pbook.setTotalPrice(number * book.getPrice());
			totalFee += pbook.getTotalPrice();
			purchasedBooks.add(pbook);
		}

		if(cart.size()==0) {
			return "redirect:/history";
		}
		
//		Lay thong tin nguoi dung
		String username = principal.getName();
		User user = userServiceImpl.findByUsername(username);
		
		DonHang donHang = new DonHang();
		donHang.setUser(user);
		donHang.setPurchasedBooks(purchasedBooks);
		donHang.setTotalPrice(totalFee);
		donHang.setOrderedAt(new Date());
		donHang.setDelivered(false);
		
		orderServiceImpl.save(donHang);
		
		cart.clearOrder();
		
		return "redirect:/history";
	}
	
}
