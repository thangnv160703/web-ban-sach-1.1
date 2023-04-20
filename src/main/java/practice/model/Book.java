package practice.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;

@Data
@Entity
public class Book {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String author;
	private int publishedYear;
	@ManyToMany(targetEntity = Genre.class, fetch = FetchType.LAZY)
	private List<Genre> genres;
	private int price;
	private String image;
	private String note;
	private boolean isForSale;
	
	public boolean hasGenres(List<Genre> genre_list) {
		for(Genre genre: genre_list) {
			if(!this.genres.contains(genre)) {
				return false;
			}
		}
		return true;
	}
}
