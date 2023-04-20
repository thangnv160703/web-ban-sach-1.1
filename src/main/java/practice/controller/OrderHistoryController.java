package practice.controller;

import java.lang.reflect.Array;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import practice.model.Book;
import practice.model.DonHang;
import practice.model.Genre;
import practice.model.User;
import practice.serviceImpl.OrderServiceImpl;
import practice.serviceImpl.UserServiceImpl;

@Controller
@RequestMapping("/history")
public class OrderHistoryController {

	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	
	@GetMapping
	public String showOrderHistory(
			@RequestParam(required = false, value = "page") Integer page, 
			Model model, Principal principal) {
//		Get username
		String username = principal.getName();
		if(username == null) {
			return "redirect:/";
		}
		User user = userServiceImpl.findByUsername(username); 
		if(user == null) {
			return "redirect:/";
		}
		List<DonHang> orders = orderServiceImpl.findByUser(user);
		Collections.sort(orders);

		int len = orders.size() / 10;
		if (orders.size() % 10 != 0) {
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
				return "redirect:/history?page=" + len;
			}
			currentPage = page;
		}
		model.addAttribute("current", currentPage);

		List<DonHang> viewOrders = new ArrayList<>();
		int start = (currentPage - 1) * 10;
		int end = (currentPage * 10);
		for (int i = start; i < end && i < orders.size(); ++i) {
			DonHang order = orders.get(i);
			order.setUser(null);
			order.setPurchasedBooks(null);
			viewOrders.add(order);
		}

		model.addAttribute("orderList", viewOrders);

		return "user/view-history";
	}
	
	@GetMapping("/view/{id}")
	public String viewOrderDetails(@PathVariable(name = "id") Long id, Model model) {
		DonHang order = orderServiceImpl.findById(id);
		if(order == null) {
			return "redirect:/history";
		}
		model.addAttribute("order", order);
		return "user/view-order-details";
	}
	
}
