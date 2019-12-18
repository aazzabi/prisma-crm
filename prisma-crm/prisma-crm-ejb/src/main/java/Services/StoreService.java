package Services;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import Entities.Address;
import Entities.Product;
import Entities.Store;
import Entities.StoreHours;

import Interfaces.IStoreServiceLocal;

import javax.ws.rs.client.ClientBuilder;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


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
		Store s= em.find(Store.class, id);
		s.setAddress(null);
		em.remove(s);

	}

	@Override
	public Store updateStore(Store newStore) {
		Store s = em.find(Store.class, newStore.getId());
		s.setName(newStore.getName());
		s.setTelephone(newStore.getTelephone());
		s.setAddress(newStore.getAddress());
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




	public float calculateDistanceBetweenTwoStores(double ORG_LON, double ORG_LAT, double DEST_LON, double DEST_LAT) {
		double earthRadius = 6371000;
		double dLat = Math.toRadians(DEST_LAT - ORG_LAT);
		double dLng = Math.toRadians(DEST_LON - ORG_LON);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(DEST_LAT))
		* Math.cos(Math.toRadians(ORG_LAT)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);
		return dist;

	}


	@Override
	public Store getNearestStoreAddress(double LON, double LAT) {

		List<Store> stores = em.createQuery("SELECT S FROM Store S", Store.class).getResultList();
		Store nearStore = stores.get(0);
		float min = calculateDistanceBetweenTwoStores(nearStore.getAddress().getLongtitude(),
				nearStore.getAddress().getLongtitude(), LON, LAT);
		if (stores.size() > 0) {
			for (int i=1;i<stores.size();i++) {
				Store s = stores.get(i);
				float result = calculateDistanceBetweenTwoStores(s.getAddress().getLongtitude(),
						s.getAddress().getLatitude(), LON, LAT);
				if (result < min) {
					min = result;
					nearStore = s;
				}
			}
		}
		return nearStore;
	}

	@Override
	public Address findAdrById(int idAdr) {
		
		return em.find(Address.class, idAdr);
	}

	@Override
	public List<Address> getAllAddress() {
		List<Address> adr = em.createQuery("from Address", Address.class).getResultList();
		return adr;
	}




}
