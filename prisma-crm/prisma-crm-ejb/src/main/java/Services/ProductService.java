package Services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import Entities.Mobile;
import Entities.Product;
import Entities.Store;
import Entities.Tariff;
import Enums.ProductType;
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

		return p;

	}

	@Override
	public Product findProductById(int id) {
		Product p = em.find(Product.class, id);
		return p;
		
	}
	
	

	@Override
	public List<Product> findProductByReference(String ref) {
		
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
	public Mobile addMobile(Mobile m) {
		em.persist(m);
		return m;
	}

	@Override
	public Mobile updateMobile(Mobile newMobile) {
		updateProduct(newMobile);
		Mobile m = em.find(Mobile.class, newMobile.getId());
		m.setBrand(newMobile.getBrand());
		m.setCamera(newMobile.getCamera());
		m.setMemory(newMobile.getMemory());
		m.setResolution(newMobile.getResolution());
		m.setSystem(newMobile.getSystem());
		m.setRam(newMobile.getRam());

		return m;
	}

	@Override
	public void assignTarifToProduct(int idProduct, int idTarif) {
		Product p = findProductById(idProduct);
		Tariff t = findTarifById(idTarif);
		p.getTarifs().add(t);
	}

	public void iterateEnum() {
		for (ProductType type : ProductType.values()) { 
			System.out.println(type); 
		}

	}

	@Override
	public List<Product> findProductsByStore(Store s ) {
		TypedQuery<Product> query = em.createQuery(
				"SELECT c FROM Product c WHERE c.store = :store", Product.class);

		return query.setParameter("store", s).getResultList();
	}

}
