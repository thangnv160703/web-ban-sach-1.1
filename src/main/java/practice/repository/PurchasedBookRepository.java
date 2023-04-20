package practice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import practice.model.PurchasedBook;

@Repository(value = "purchasedBookRepo")
public interface PurchasedBookRepository extends CrudRepository<PurchasedBook, Long>{

}
