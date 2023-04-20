package practice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Comment implements Comparable<Comment>{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne(targetEntity = User.class)
	private User user;
	@ManyToOne(targetEntity = Book.class)
	private Book book;
	private String comment;
	
	@Override
	public int compareTo(Comment o) {
		if(this.id > o.id) {
			return -1;
		}
		if(this.id < o.id) {
			return 1;
		}
		return 0;
	}
}
