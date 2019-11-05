package Interfaces;



import javax.ejb.Local;


import Entities.ProviderOrder;
import Entities.Stock;
import Entities.Store;

@Local
public interface IStockServiceLocal {
	
	public int calculRecentQuantity(Store store);
	
	public String addStock(int idStore,int idProduct,Stock stock) ;
	
	public Stock addStockFromProvider(int idStore,int idProduct,Stock stock) ;
	
	public ProviderOrder addProviderOrder(ProviderOrder order);
	
	public void sendJavaMail(ProviderOrder order,Stock stock) ;
	
	public Stock checkStock(int idStore,int idProduct);
	
	public Stock updateStock(Stock newStock);
	
	public Stock updateStockProvider(int idStock, int addedQuantity);
	
	public Stock findStockById(int idStock);

}
