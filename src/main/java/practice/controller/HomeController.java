package practice.controller;

import java.security.Principal;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import practice.model.User;
import practice.serviceImpl.UserServiceImpl;

@Controller
public class HomeController {

	@Autowired
	private UserServiceImpl userServiceImpl;

	@GetMapping("/")
	public String home(Principal principal) {
		if (principal == null) {
			return "general/home";
		}
		String username = principal.getName();
		User user = userServiceImpl.findByUsername(username);
		if (user.getRole().equals("ADMIN")) {
			return "redirect:/admin";
		} else {
			return "redirect:/user";
		}
	}
	
//	@GetMapping("/test")
//	public String testForm(Model model) {
//		User user = new User();
//		model.addAttribute("user", user);
//		return "test";
//	}
//	
//	@PostMapping("/test")
//	public String testFormResolve(@ModelAttribute("user") User user) {
//		System.out.println(user.toString());
//		return "redirect:/test";
//	}
	
	@GetMapping("/user")
	public String userHomepage() {
		return "user/user-home";
	}
	
	@GetMapping("/admin")
	public String adminHomepage() {
		return "admin/admin-home";
	}

//	@GetMapping("/login")
//	public String login() {
//		return "general/login";
//	}
	
//	@PostMapping("/login")
//	public String loginResolve(
//			@RequestParam(name = "username") String username,
//			@RequestParam(name = "password") String password,
//			Model model) {
//		User user = userServiceImpl.findByUsername(username);
//		
//		if(user==null || !user.getPassword().equals(password)) {
//			model.addAttribute("message", "Thong tin sai.");
//		}
//		else {
//			if(user.isActive()) {
//				return "redirect:/user";
//			}
//			model.addAttribute("message", "Tai khoan da bi khoa.");
//		}
//		return "general/login";
//	}

	@GetMapping("/register")
	public String register(Model model) {
		User customer = new User();
		model.addAttribute("user", customer);
		return "general/register";
	}

	@PostMapping("/register")
	public String createNewAccount(@ModelAttribute("user") User user, Model model) {
//		System.out.println("Received");
		User existedUser = userServiceImpl.findByUsername(user.getUsername());

		if (existedUser != null) {
			model.addAttribute("message", "Username da co nguoi su dung, chon username khac.");
			model.addAttribute("user", user);
			return "general/register";
		}

		user.setActive(true);
		user.setRole("USER");
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		
		User savedUser = userServiceImpl.save(user);
//		System.out.println(savedUser.toString());
		
		return "general/register-success";
	}
	
	@GetMapping("/account-info")
	public String viewAccountInfo(Model model, Principal principal) {
		String username = principal.getName();
		User account = userServiceImpl.findByUsername(username);
		model.addAttribute("account", account);
		return "general/view-account";
	}
	
	@GetMapping("/changeinfo/{id}")
	public String changeAccountInfo(@PathVariable(name = "id") Long id, Model model) {
		User account = userServiceImpl.findById(id);
		model.addAttribute("account", account);
		return "general/change-account-info";
	}
	
	@PostMapping("/changeinfo")
	public String changeAccountInfoResolve(@ModelAttribute("account") User account, Model model) {
		User existedUser = userServiceImpl.findByUsername(account.getUsername());

		if (existedUser != null && existedUser.getId() != account.getId()) {
			model.addAttribute("message", "Username da co nguoi su dung, chon username khac.");
			model.addAttribute("account", account);
			return "general/change-account-info";
		}

		account.setActive(true);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(account.getPassword());
		account.setPassword(encodedPassword);
		
		userServiceImpl.save(account);
		return "redirect:/logout";
	}

}
