package practice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import practice.model.Genre;

@Repository(value = "genreRepo")
public interface GenreRepository extends CrudRepository<Genre, Long>{

	Genre findByNameIgnoreCase(String name);
	
}
