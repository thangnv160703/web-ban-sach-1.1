package practice.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class DonHang implements Comparable<DonHang>{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@ManyToOne(targetEntity = User.class)
	private User user;
	@OneToMany(targetEntity = PurchasedBook.class, cascade = CascadeType.ALL)
	private List<PurchasedBook> purchasedBooks;
	private int totalPrice;
	private Date orderedAt;
	private Date deliveredAt;
	private boolean isDelivered;
	
	@Override
	public int compareTo(DonHang o) {
		if(this.id > o.id) {
			return -1;
		}
		if(this.id < o.id) {
			return 1;
		}
		return 0;
	}
}
