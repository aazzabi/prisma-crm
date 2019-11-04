package Interfaces;

import java.util.List;

import javax.ejb.Local;

import Entities.Product;
import Entities.Store;
import Entities.Tariff;

@Local
public interface IProductServiceLocal {

	public Product addProduct(Product p);

	public void removeProduct(int id);

	public Product updateProduct(Product newProduct);

	public Product findProductById(int id);	
	
	public List<Product> findProductsByReference(String ref);
	
	public List<Product> findProductsByStore(Store store);

	public List<Product> findAllProducts();

	public Tariff addTarif(Tariff t);

	public void removeTarif(int id);

	public Tariff updateTarif(Tariff newTarif);

	public Tariff findTarifById(int id);

	public List<Tariff> findAllTarifs();
	
	public void assignTarifToProduct(int idProduct, int idTarif);

}
