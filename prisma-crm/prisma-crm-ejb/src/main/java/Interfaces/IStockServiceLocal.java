package Interfaces;



import javax.ejb.Local;


import Entities.ProviderOrder;
import Entities.Stock;
import Entities.Store;

@Local
public interface IStockServiceLocal {
	
	public int calculRecentQuantity(Store store);
	
	public String addStock(Stock stock,Store store);
	
	public ProviderOrder addProviderOrder(ProviderOrder order);
	
	public void sendJavaMail(ProviderOrder order) ;
	
	public Stock checkStock(int idStore,String ref);
	
	public Stock updateStock(Stock newStock);

}
