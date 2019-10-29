package Interfaces;

import java.util.List;

import javax.ejb.Local;

import Entities.Store;
import Entities.StoreHours;


@Local
public interface IStoreServiceLocal {

	public Store addStore(Store s);

	public void removeStore(int id);

	public Store updateStore(Store newStore);

	public Store findStoreById(int id);

	public List<Store> findAllStores();
	
	public StoreHours addStoreTime(StoreHours sh);

	public void removeStoreTime(int id);

	public StoreHours updateStoreTime(StoreHours newSH);

	public StoreHours findStoreTimeById(int id);

	public List<StoreHours> findAllStoreTimes();
	
	public void assignTimeToStore(int idStore,int idTime);
	
	public Store assignProductToStore(int idStore,int idProduct);
	

}
