package practice.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Scope("session")
@Data
public class Cart {
	
	private Map<Long, Integer> bookIds = new HashMap<Long, Integer>();
	
	public void addBooks(Long id) {
		if(!this.bookIds.containsKey(id)) {
			bookIds.put(id, 1);
		}
	}
	
	public void removeBook(Long id) {
		this.bookIds.remove(id);
	}
	
	public void clearOrder() {
		this.bookIds.clear();
	}
	
	public void orderNumber(Long id, int number) {
		this.bookIds.put(id, number);
	}
	
	public int size() {
		return this.bookIds.size();
	}
	
}
