package practice.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import practice.model.Genre;
import practice.repository.GenreRepository;

@Service
public class GenreServiceImpl {

	@Autowired
	private GenreRepository genreRepo;
	
	public List<Genre> findAll() {
		List<Genre> genres = (List<Genre>) genreRepo.findAll();
		return genres;
	}
	
	public Genre findByName(String name) {
		Genre genre = genreRepo.findByNameIgnoreCase(name);
		return genre;
	}
	
	public Genre findById(Long id) {
		Genre genre = genreRepo.findById(id).orElse(null);
		return genre;
	}
	
	public List<Genre> findAllById(List<Long> ids) {
		List<Genre> genres = (List<Genre>) genreRepo.findAllById(ids);
		return genres;
	}
	
	public Genre save(Genre genre) {
		Genre savedGenre = genreRepo.save(genre);
		return savedGenre;
	}
	
	public void delete(Long id) {
		genreRepo.deleteById(id);
	}
}
