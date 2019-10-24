package Interfaces;

import java.util.List;

import javax.ejb.Local;

import Entities.Store;


@Local
public interface IStoreServiceLocal {

	public Store addStore(Store s);

	public void removeStore(int id);

	public Store updateStore(Store newStore);

	public Store findStoreById(int id);

	public List<Store> findAllStores();
	

}
