package it.polimi.db2.gma.services;

import it.polimi.db2.gma.entities.Offensive_word;
import it.polimi.db2.gma.entities.Product;
import it.polimi.db2.gma.exceptions.OffensiveWordException;
import it.polimi.db2.gma.exceptions.ProductException;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import java.util.List;
import java.util.Locale;

@Stateless
public class ProductService {
	@PersistenceContext(unitName = "GMA_EJB")
	private EntityManager em;

	public ProductService() {
	}

	public List<Product> findAllProducts () throws ProductException {
		List<Product> products = null;
		try {
			products = em.createNamedQuery("Product.findAll", Product.class).getResultList();
		} catch (PersistenceException e) {
			throw new ProductException("Could not load the product");
		}
		return products;
	}

	public void createProduct(String name, byte[] image){
		Product product = new Product(name, image);
		em.persist(product);
	}
}
