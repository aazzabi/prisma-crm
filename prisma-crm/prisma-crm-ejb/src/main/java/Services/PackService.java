package Services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import Entities.Pack;
import Entities.Product;
import Entities.Promotion;
import Interfaces.IPack;

@Stateless
public class PackService implements IPack {

	@PersistenceContext
	EntityManager em;

	@Override
	public Pack addpack(Pack pack) {
		pack.setPrice(0);
		em.persist(pack);
		return pack;
	}

	@Override
	public void deletePack(int id) {
		em.remove(em.find(Pack.class, id));

	}

	@Override
	public Pack findpack(int id) {
		return em.find(Pack.class, id);

	}

	@Override
	public Pack updatepack(Pack pack) {
		Pack p= em.find(Pack.class, pack.getId());
		p.setDescription(pack.getDescription());
		p.setName(pack.getName());
		p.setPrice(pack.getPrice());
		
		
		return p;

	}

	

	@Override
	public List<Product> getAllProductPerPack(int id) {
		Pack p = em.find(Pack.class, id);
		return p.getProducts();
	}

	@Override
	public void addproductpack(int idp, int idpa) {
		Product pr = em.find(Product.class, idp);
		Pack pa = em.find(Pack.class, idpa);
		List<Pack> lpack = pr.getPacks();
		List<Product> lprod = pa.getProducts();

		if (pr.getNew_price() == 0) {
		pa.setPrice(pa.getPrice() + pr.getPrice());}
	else {
		pa.setPrice(pa.getPrice() + pr.getNew_price());

            }
		lpack.add(pa);
		lprod.add(pr);
		pr.setPacks(lpack);
		pa.setProducts(lprod);

	}

}
