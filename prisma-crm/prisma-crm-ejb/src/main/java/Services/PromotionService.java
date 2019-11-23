package Services;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.Product;
import Entities.Promotion;
import Interfaces.IPromotion;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

@Stateless
public class PromotionService implements IPromotion {

	public static final String ACCOUNT_SID = "AC2511691a914b9769651c05bfb2d3f129";
	public static final String AUTH_TOKEN = "2db36d86149fe7ebb4f2c733dbee2287";
	@PersistenceContext
	EntityManager em;

	@Override
	public void addPromotion(Promotion p) {
		em.persist(p);
	}

	@Override
	public void deletePromotion(int id) {
		em.remove(em.find(Promotion.class, id));

	}

	@Override
	public Promotion findPromotion(int id) {
		return em.find(Promotion.class, id);
	}

	@Override
	public Promotion updatePromotion(Promotion promotion) {
		Promotion p = em.find(Promotion.class, promotion.getId());
		p.setE_date(promotion.getE_date());
		p.setPercentage(promotion.getPercentage());
		p.setPeriod(promotion.getPeriod());
		p.setS_date(promotion.getS_date());

		return p;

	}

	@Override
	public void passerenpromotion(int idp, int idpr) {
		Product pr = em.find(Product.class, idpr);
		Promotion p = em.find(Promotion.class, idp);
		pr.setNew_price(0);
		pr.setPromotion(null);

		pr.setPromotion(p);

		pr.setNew_price(pr.getPrice() - (pr.getPrice() * p.getPercentage()) / 100);

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

		Message message = Message

				
				.creator(new PhoneNumber("+21629684222"), new PhoneNumber("+18652719262"), "  Chère cliente, cher client on a une Nouvelle promotion pour vous  en  "
						+ pr.getName() + "  de " + p.getPercentage() +" % "+ "  le nouveau prix est  " + pr.getNew_price()+" .Pour plus d'information visiter notre site Web opérateur.com ")
				.create();
		System.out.println(message.getSid());

	}

	@Override
	public List<Product> listedesProduitpromotion() {
		this.deleteOldPrmotions();
		List<Product> productspro = em
				.createQuery("SELECT p FROM Product p WHERE p.promotion IS NOT NULL", Product.class).getResultList();

		return productspro;
	}

	@Override
	public List<Promotion> listepromotions() {
		this.deleteOldPrmotions();
		List<Promotion> promotions = em.createQuery("FROM Promotion", Promotion.class).getResultList();

		return promotions;
	}

	@Override
	public List<Promotion> getOldPromotions() {
		List<Promotion> promotions = em.createQuery("Select p FROM Promotion p where p.e_date <:d ", Promotion.class)
				.setParameter("d", this.getDateNow()).getResultList();

		return promotions;
	}

	public void deleteOldPrmotions() {
		List<Promotion> listPromotions = this.getOldPromotions();
		for (Promotion prom : listPromotions) {
			List<Product> listProd = prom.getProducts();
			for (Product prod : listProd) {
				prod.setNew_price(0);
				prod.setPromotion(null);
			}
			this.deletePromotion(prom.getId());
		}
	}

	public Date getDateNow() {
		SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String pattern = "yyyy-MM-dd HH:mm:ss.SSS";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fr", "FR"));

		String date = simpleDateFormat.format(new Date());
		System.out.println("DATE " + date);

		java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(date);
		java.util.Date improperUtilDate = sqlTimestamp;

		return improperUtilDate;
	}
	
}
