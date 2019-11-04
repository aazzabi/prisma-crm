package Services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import Entities.Product;
import Entities.Store;
import Entities.Tariff;
import Interfaces.IProductServiceLocal;
import Interfaces.IProductServiceRemote;

@Stateless
public class ProductService implements IProductServiceLocal, IProductServiceRemote {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager em;

	@Override
	public Product addProduct(Product p) {
		em.persist(p);
		return p;
	}

	@Override
	public void removeProduct(int id) {
		em.remove(em.find(Product.class, id));
	}

	@Override
	public Product updateProduct(Product newProduct) {

		Product p = em.find(Product.class, newProduct.getId());
		p.setName(newProduct.getName());
		p.setReference(newProduct.getReference());
		p.setDescription(newProduct.getDescription());
		p.setType(newProduct.getType());
		p.setGuarantee(newProduct.getGuarantee());
		p.setPrice(newProduct.getPrice());
		p.setBrand(newProduct.getBrand());
		p.setCamera(newProduct.getCamera());
		p.setImageUrl(newProduct.getImageUrl());
		p.setMemory(newProduct.getMemory());
		p.setResolution(newProduct.getResolution());
		
		
		
		return p;

	}

	@Override
	public Product findProductById(int id) {
		Product p = em.find(Product.class, id);
		return p;
		
	}
	@Override
	public List<Product> findProductsByStore(Store s ) {
		TypedQuery<Product> query = em.createQuery(
				"SELECT c FROM Product c WHERE c.store = :store", Product.class);

		return query.setParameter("store", s).getResultList();
	}


	

	@Override
	public List<Product> findProductsByReference(String ref) {
		
		TypedQuery<Product> query = em.createQuery(
				"SELECT c FROM Product c WHERE c.reference = :ref", Product.class);

		return query.setParameter("ref", ref).getResultList();
	}

	@Override
	public List<Product> findAllProducts() {
		List<Product> products = em.createQuery("from Product", Product.class).getResultList();

		

		return products;
	}

	@Override
	public Tariff addTarif(Tariff t) {
		em.persist(t);
		return t;
	}

	@Override
	public void removeTarif(int id) {
		em.remove(em.find(Tariff.class, id));

	}

	@Override
	public Tariff updateTarif(Tariff newTarif) {
		Tariff t = em.find(Tariff.class, newTarif.getId());
		t.setCnxSpeed(newTarif.getCnxSpeed());
		t.setPriceT(newTarif.getPriceT());
		return t;

	}

	@Override
	public Tariff findTarifById(int id) {
		Tariff t = em.find(Tariff.class, id);
		return t;
	}

	@Override
	public List<Tariff> findAllTarifs() {
		List<Tariff> tarifs = em.createQuery("from Tariff", Tariff.class).getResultList();
		return tarifs;
	}


	@Override
	public void assignTarifToProduct(int idProduct, int idTarif) {
		Product p = findProductById(idProduct);
		Tariff t = findTarifById(idTarif);
		p.getTarifs().add(t);
	}




}
