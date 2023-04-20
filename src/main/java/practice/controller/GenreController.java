package practice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;
import practice.model.Genre;
import practice.serviceImpl.GenreServiceImpl;

@Controller
@RequestMapping("/genre")
public class GenreController {

	@Autowired
	private GenreServiceImpl genreServiceImpl;

	@GetMapping(value = { "", "/{page}" })
	public String showAllGenres(@RequestParam(required = false, name = "page") Integer page, Model model) {
		List<Genre> genre_list = genreServiceImpl.findAll();

		int len = genre_list.size() / 10;
		if (genre_list.size() % 10 != 0) {
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
				return "redirect:/genre?page=" + len;
			}
			currentPage = page;
		}
		model.addAttribute("current", currentPage);

		List<Genre> viewedGenres = new ArrayList<>();
		int start = (currentPage - 1) * 10;
		int end = (currentPage * 10);
		for (int i = start; i < end && i < genre_list.size(); ++i) {
			viewedGenres.add(genre_list.get(i));
		}

		model.addAttribute("genre_list", viewedGenres);

		return "admin/manage-genre";
	}

	@GetMapping("/add")
	public String addGenre(Model model) {
		model.addAttribute("genre", new Genre());
		return "admin/add-genre";
	}

	@PostMapping("/add")
	public String addGenreResolve(@ModelAttribute(name = "genre") Genre genre, Model model) {
		Genre existedGenre = genreServiceImpl.findByName(genre.getName());
		if (existedGenre != null) {
			model.addAttribute("message", "The loai da ton tai!");
			return "admin/add-genre";
		}
		genreServiceImpl.save(genre);
		return "redirect:/genre";
	}

	@GetMapping("/edit/{id}")
	public String editGenre(@PathVariable(name = "id") Long id, Model model) {
		Genre genre = genreServiceImpl.findById(id);
		model.addAttribute("genre", genre);
		return "admin/edit-genre";
	}

	@PostMapping("/edit")
	public String editGenreResolve(@ModelAttribute(name = "genre") Genre genre, Model model) {
		Genre existedGenre = genreServiceImpl.findByName(genre.getName());
		if (existedGenre != null) {
			model.addAttribute("message", "The loai da ton tai!");
			return "admin/edit-genre";
		}
		genreServiceImpl.save(genre);
		return "redirect:/genre";
	}

	@GetMapping("/delete/{id}")
	public String deleteGenre(@PathVariable(name = "id") Long id) {
		genreServiceImpl.delete(id);
		return "redirect:/genre";
	}
}
