package Services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.fasterxml.jackson.core.JsonParser;

import Entities.Product;
import Entities.Store;
import Entities.StoreHours;
import Interfaces.IProductServiceLocal;
import Interfaces.IStoreServiceLocal;

@Stateless
public class StoreService implements IStoreServiceLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager em;
	

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

	@Override
	public StoreHours addStoreTime(StoreHours sh) {
		em.persist(sh);
		return sh;
	}

	@Override
	public void removeStoreTime(int id) {
		em.remove(em.find(StoreHours.class, id));

	}

	@Override
	public StoreHours updateStoreTime(StoreHours newSH) {
		StoreHours sh = em.find(StoreHours.class, newSH.getId());
		sh.setDay(newSH.getDay());
		sh.setOpenAt(newSH.getOpenAt());
		sh.setCloseAt(newSH.getCloseAt());
		return sh;
	}

	@Override
	public StoreHours findStoreTimeById(int id) {
		StoreHours sh = em.find(StoreHours.class, id);
		return sh;
	}

	@Override
	public List<StoreHours> findAllStoreTimes() {
		List<StoreHours> hours = em.createQuery("from StoreHours", StoreHours.class).getResultList();
		return hours;
	}

	@Override
	public void assignTimeToStore(int idStore, int idTime) {
		StoreHours sh = findStoreTimeById(idTime);
		Store str = findStoreById(idStore);
		sh.setStore(str);
	}

	@Override
	public Store assignProductToStore(int idStore, int idProduct) {
		Store s = findStoreById(idStore);
		Product p = em.find(Product.class, idProduct);
		
		s.getProducts().add(p);
		p.setStore(s);
		
		
		return s;
		
	}

}
