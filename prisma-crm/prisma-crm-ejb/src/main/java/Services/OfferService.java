package Services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import Entities.Offer;
import Interfaces.IOffer;

@Stateless
public class OfferService implements IOffer {

	@PersistenceContext
	EntityManager em;

	@Override
	public Offer addOffer(Offer o) {
		em.persist(o);
		return o;
	}

	@Override
	public void deleteOffer(int id) {
		em.remove(em.find(Offer.class, id));
	}

	@Override
	public Offer findOffer(int id) {
		return em.find(Offer.class, id);
	}

	@Override
	public Offer updateOffer(Offer offer,int id) {

		Offer o = em.find(Offer.class,id);
		o.setAvantages(offer.getAvantages());
		o.setDescription(offer.getDescription());
		o.setName(offer.getName());
		o.setOfftype(offer.getOfftype());
		o.setTarification(offer.getTarification());
		return o;

	}

	@Override
	public List<Offer> listeoffer() {
		List<Offer> offers = em.createQuery("from Offer", Offer.class).getResultList();

		return offers;
	}
	
}
