package practice.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import practice.model.DonHang;
import practice.model.User;
import practice.serviceImpl.OrderServiceImpl;
import practice.serviceImpl.UserServiceImpl;

@Controller
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@Autowired
	private OrderServiceImpl orderServiceImpl;
	
	@GetMapping
	public String manageOrder(
			@RequestParam(required = false, value = "page") Integer page, 
			@RequestParam(defaultValue = "", name = "username") String username,
			@RequestParam(defaultValue = "", name = "name") String name,
			@RequestParam(required = false, name = "isDelivered") Boolean isDelivered,
			Model model) {
		
		List<User> users = userServiceImpl.findByUsernameName(username, name);
		List<DonHang> searchedOrders = new ArrayList<>();
		for(User user: users) {
			List<DonHang> orders = new ArrayList<DonHang>();
			if(isDelivered == null) {
				orders = orderServiceImpl.findByUser(user);
			}
			else {
				orders = orderServiceImpl.findByUserDelivered(user, isDelivered);
			}
			searchedOrders.addAll(orders);
		}
		Collections.sort(searchedOrders);

		int len = searchedOrders.size() / 10;
		if (searchedOrders.size() % 10 != 0) {
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
				return "redirect:/order?page=1";
			}
			currentPage = page;
		}
		model.addAttribute("current", currentPage);

		List<DonHang> viewOrders = new ArrayList<>();
		int start = (currentPage - 1) * 10;
		int end = (currentPage * 10);
		for (int i = start; i < end && i < searchedOrders.size(); ++i) {
			DonHang order = searchedOrders.get(i);
			order.setPurchasedBooks(null);
			viewOrders.add(order);
		}
		model.addAttribute("orderList", viewOrders);
		
		model.addAttribute("searchedName", name);
		model.addAttribute("searchedUsername", username);
		model.addAttribute("delivered", isDelivered);
		return "admin/manage-order";
	}
	
	@GetMapping("/delete")
	public String deleteOrder(@RequestParam(name = "id") Long id,
			@RequestParam(required = false, value = "page") Integer page, 
			@RequestParam(defaultValue = "", name = "username") String username,
			@RequestParam(defaultValue = "", name = "name") String name,
			@RequestParam(required = false, name = "isDelivered") Boolean isDelivered) {
		orderServiceImpl.delete(id);
		if(page==null) {
			page=1;
		}
		String url;
		if(isDelivered!=null) {
			url = "redirect:/order?page=" + page + "&username=" + username + "&name=" + name + "&isDelivered=" + isDelivered;
		} else {
			url = "redirect:/order?page=" + page + "&username=" + username + "&name=" + name;
		}
		return url;
	}
	
	@GetMapping("/deliver")
	public String deliverOrder(@RequestParam(name = "id") Long id,
			@RequestParam(required = false, value = "page") Integer page, 
			@RequestParam(defaultValue = "", name = "username") String username,
			@RequestParam(defaultValue = "", name = "name") String name,
			@RequestParam(required = false, name = "isDelivered") Boolean isDelivered) {
		DonHang order = orderServiceImpl.findById(id);
		order.setDelivered(true);
		order.setDeliveredAt(new Date());
		orderServiceImpl.save(order);
		
		if(page==null) {
			page=1;
		}
		String url;
		if(isDelivered != null) {
			url = "redirect:/order?page=" + page + "&username=" + username + "&name=" + name + "&isDelivered=" + isDelivered;
		} else {
			url = "redirect:/order?page=" + page + "&username=" + username + "&name=" + name + "&isDelivered=";
		}
		return url;
	}
	
	@GetMapping("/detail")
	public String manageOrderDetail(
			@RequestParam(name = "id") Long id,
			@RequestParam(required = false, value = "page") Integer page, 
			@RequestParam(defaultValue = "", name = "username") String username,
			@RequestParam(defaultValue = "", name = "name") String name,
			@RequestParam(required = false, name = "isDelivered") Boolean isDelivered, Model model) {
		DonHang order = orderServiceImpl.findById(id);
		model.addAttribute("order", order);
		
		model.addAttribute("page", page);
		model.addAttribute("searchedUsername", username);
		model.addAttribute("searchedName", name);
		model.addAttribute("delivered", isDelivered);
		return "admin/view-order";
	}
	
}
