package practice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import practice.model.Book;
import practice.model.Genre;
import practice.model.User;
import practice.serviceImpl.UserServiceImpl;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private UserServiceImpl userServiceImpl;

	@GetMapping
	public String viewAccounts(
			@RequestParam(required = false, value = "page") Integer page, 
			Model model) {
		
		List<User> user_list = userServiceImpl.findByRole("USER");

		int len = user_list.size() / 10;
		if (user_list.size() % 10 != 0) {
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
				return "redirect:/account?page=" + len;
			}
			currentPage = page;
		}

		model.addAttribute("current", currentPage);

		List<User> viewedUsers = new ArrayList<>();
		int start = (currentPage - 1) * 10;
		int end = (currentPage * 10);
		for (int i = start; i < end && i < user_list.size(); ++i) {
			User user = user_list.get(i);
			user.setPassword(null);
			viewedUsers.add(user);
		}

		model.addAttribute("user_list", viewedUsers);

		return "admin/manage-user";
	}
	
	@GetMapping("/disable/{id}")
	public String disableAccount(@PathVariable(name = "id") Long id) {
		User user = userServiceImpl.findById(id);
		if(user != null) {
			user.setActive(false);
			userServiceImpl.save(user);
		}
		return "redirect:/account";
	}
	
	@GetMapping("/enable/{id}")
	public String enableAccount(@PathVariable(name = "id") Long id) {
		User user = userServiceImpl.findById(id);
		if(user != null) {
			user.setActive(true);
			userServiceImpl.save(user);
		}
		return "redirect:/account";
	}
	
}
