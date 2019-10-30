package Services;

import java.util.List;


import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import Entities.Product;
import Entities.Promotion;
import Interfaces.IPromotion;

@Stateless
public class PromotionService  implements IPromotion{
	@PersistenceContext
    EntityManager em; 
	
	@Override
	public void addPromotion(Promotion p) {
		em.persist(p);

		
	}

	@Override
	public void deletePromotion(int id) {
		em.remove(em.find(Promotion.class, id ));
		
	}

	@Override
	public Promotion findPromotion(int id) {
		return em.find(Promotion.class, id );
	}

	@Override
	public Promotion updatePromotion(Promotion promotion) {
		Promotion p= em.find(Promotion.class, promotion.getId());
		p.setE_date(promotion.getE_date());
		p.setPercentage(promotion.getPercentage());
		p.setPeriod(promotion.getPeriod());
		p.setS_date(promotion.getS_date());
		
		return p;



	}
	
	@Override
	public void addPromotiontoproduct(int idp, int idpr) {
		Product pr=em.find(Product.class, idpr);
		Promotion p = em.find(Promotion.class, idp );
		
		pr.setPromotion(p);

		pr.setNew_price(pr.getPrice()-(pr.getPrice()*p.getPercentage())/100);
			
		
	
		
	}

	
	
	@Override
	public List<Product> listedesProduitpromotion() {
		List<Product> productspro = em.createQuery("SELECT p FROM Product p WHERE p.promotion IS NOT NULL", Product.class).getResultList();

		return productspro;
	}

	@Override
	public List<Promotion> listepromotions() {
		List<Promotion> promotions = em.createQuery("FROM Promotion", Promotion.class).getResultList();

		return promotions;
	}

	
	
}
