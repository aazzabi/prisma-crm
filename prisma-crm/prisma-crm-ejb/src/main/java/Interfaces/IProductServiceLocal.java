package Interfaces;

import java.util.List;

import javax.ejb.Local;

<<<<<<< HEAD
import Entities.Mobile;
import Entities.Product;
import Entities.Store;
=======
import Entities.Product;
>>>>>>> f09d376b1f622471b8f62e53f21f8b05c844cb7e
import Entities.Tariff;

@Local
public interface IProductServiceLocal {

	public Product addProduct(Product p);

	public void removeProduct(int id);

	public Product updateProduct(Product newProduct);

	public Product findProductById(int id);
	
	public List<Product> findProductByReference(String ref);
	
	public List<Product> findProductsByStore(Store store);

	public List<Product> findAllProducts();

	public Tariff addTarif(Tariff t);

	public void removeTarif(int id);

	public Tariff updateTarif(Tariff newTarif);

	public Tariff findTarifById(int id);

	public List<Tariff> findAllTarifs();

	public Mobile addMobile(Mobile m);

	public Mobile updateMobile(Mobile m);
	
	public void assignTarifToProduct(int idProduct, int idTarif);

}
