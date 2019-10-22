package Services;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.Store;
import Interfaces.IStoreServiceLocal;

@Stateless
public class StoreService implements IStoreServiceLocal{
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager em;
	

	public StoreService() {
		super();
	}

	@Override
	public Store addStore(Store s) {
		em.persist(s);
		return s;
	}

	@Override
	public void removeStore(int id) {
		em.remove(em.find(Store.class, id));	
		
	}

	@Override
	public Store updateStore(Store newStore) {
		Store s = em.find(Store.class, newStore.getId());
		s.setName(newStore.getName());
		s.setAddresStr(newStore.getAddresStr());
		s.setTelephone(newStore.getTelephone());
		return s;
	}

	@Override
	public Store findStoreById(int id) {
		Store s = em.find(Store.class, id);
		return s;
	}

	@Override
	public List<Store> findAllStores() {
		List<Store> stores = em.createQuery("from Store", Store.class).getResultList();
		return stores;
	}

}
