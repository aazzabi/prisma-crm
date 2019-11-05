package Services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import Entities.Product;
import Entities.ProviderOrder;
import Entities.Stock;
import Entities.Store;
import Interfaces.IProductServiceLocal;
import Interfaces.IStockServiceLocal;
import Interfaces.IStoreServiceLocal;
import Utils.JavaMailUtil;

@Stateless
public class StockService implements IStockServiceLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager em;

	@EJB
	IProductServiceLocal ps;
	
	@EJB
	IStoreServiceLocal ss;

	@Override
	public int calculRecentQuantity(Store store) {

		return ps.findProductsByStore(store).size();
	}

	/*@Override
	public String addStock(Stock stock,Store store) {
		int q =stock.getRecentQuantity();

		int sommeQuantite = q + stock.getQuantity();
		if (sommeQuantite > stock.getStore().getCapacity()) {
			
			return "invalide quantity, capacity="+store.getCapacity()+" recent quantity="+q;
		}

		else {
			TypedQuery<Product> query = em.createQuery(
					"SELECT p FROM Product p WHERE p.store = null and p.reference = :ref", Product.class);

			List<Product> list= query.setParameter("ref", stock.getProductRef()).getResultList();
			System.out.println("size:===" +list.size());
			
			if (list.size()>= stock.getQuantity()) {
				em.persist(stock);
				for(int i=1;i<=stock.getQuantity();i++) {
					ss.assignProductToStore(store.getId(), list.get(i).getId());
				}
				
			}
			else {
				for(Product p: list) {
					ss.assignProductToStore(store.getId(), p.getId());
				}
				
				int nbrComand = stock.getQuantity() - list.size();
				ProviderOrder order = new ProviderOrder();
				order.setProductRef(stock.getProductRef());
				order.setQuantity(nbrComand);
				order.setStore(store);
				order.setState("untreated");
				addProviderOrder(order);
				sendJavaMail(order);
				
			}
			

			em.persist(stock);
			return "order sended";
		}

		
	}*/
	
	
	@Override
	public String addStock(int idStore,int idProduct,Stock stock) {
		Store store=ss.findStoreById(idStore);
		Product product= ps.findProductById(idProduct);
		stock.setProduct(product);
		stock.setStore(store);
		if(stock.getQuantity()>store.getCapacity()) {
			return "Error: Product quantity must be less than the store capacity";
		}
		em.persist(stock);
		return "added";
	}
	
	
	@Override
	public Stock addStockFromProvider(int idStore, int idProduct, Stock stock) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public void sendJavaMail(ProviderOrder order, Stock stock) {
		try {
			JavaMailUtil.sendMail("provider.prisma@gmail.com", "Products Order",
					"<h3>Products Order</h3>"
					+"<p>Store Name :"+order.getStore().getName()+"</p>"
					+"<p>Product Reference :"+order.getProduct().getId()+"</p>"
					+"<p>Quantity :"+order.getQuantity()+"</p>"
					+"<a href='http://localhost:9080/prisma-crm-web/stock/update_stock_provider?idStock="+stock.getId()+"&&addedQuantity="+order.getQuantity()+"'>Accept the order</a>"
					);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}



	@Override
	public ProviderOrder addProviderOrder(ProviderOrder order) {
		em.persist(order);
		return order;
	}

	@Override
	public Stock checkStock(int idStore,int idProduct) {
		Store store=ss.findStoreById(idStore);
		Product product= ps.findProductById(idProduct);
		TypedQuery<Stock> query = em.createQuery(
				"SELECT s FROM Stock s WHERE s.store = :store and s.product = :product", Stock.class);

		query.setParameter("store", store);
		List<Stock> list= query.setParameter("product", product).getResultList();
		Stock stock = list.get(0);
		stock.setRecentQuantity(stock.getRecentQuantity()-1);
		updateStock(stock);
		if(stock.getRecentQuantity()==stock.getQuantityMin()) {
			int addedQuantity= stock.getQuantity()-stock.getRecentQuantity();
			ProviderOrder order = new ProviderOrder();
			order.setProduct(product);
			order.setStore(store);
			order.setQuantity(addedQuantity);
			order.setState("untreated");
			sendJavaMail(order,stock);
		}
		return stock;
		
	}

	@Override
	public Stock updateStock(Stock newStock) {
		Stock s = em.find(Stock.class, newStock.getId());
		s.setProduct(newStock.getProduct());
		s.setQuantity(newStock.getQuantity());
		s.setQuantityMin(newStock.getQuantityMin());
		s.setRecentQuantity(newStock.getRecentQuantity());
		s.setStore(newStock.getStore());
		return s;
	}

	@Override
	public Stock updateStockProvider(int idStock, int addedQuantity) {
		Stock stock = findStockById(idStock);
		stock.setRecentQuantity(stock.getRecentQuantity()+addedQuantity);
		return stock;
	}

	@Override
	public Stock findStockById(int idStock) {
		Stock s = em.find(Stock.class, idStock);
		return s;
	}



}
