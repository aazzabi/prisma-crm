package Services;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
	public int calculRecentQuantityByStore(Store store) {
		TypedQuery<Stock> query = em.createQuery(
				"SELECT s FROM Stock s WHERE s.store = :store", Stock.class);

		List<Stock> list= query.setParameter("store", store).getResultList();

		int nb=0;
		for(Stock s:list) {
			nb=nb+s.getRecentQuantity();
		}

		System.out.println("recent quantity in store=== "+nb);
		return nb;
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
		if(stock.getQuantityMin()>=stock.getQuantity()) {
			return "QuantityMin must be less than Quantity";
		}
		Store store=ss.findStoreById(idStore);
		Product product= ps.findProductById(idProduct);
		stock.setProduct(product);
		stock.setStore(store);
		stock.setRecentQuantity(stock.getQuantity());
		if(stock.getQuantity()+calculRecentQuantityByStore(store)>store.getCapacity()) {
			return "Product quantity must be less than the store capacity";
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
							+"<p>Product Name :"+order.getProduct().getName()+"</p>"
							+"<p>Product Reference :"+order.getProduct().getReference()+"</p>"
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
		
		checkStockDateQuantity(stock);
		if(stock.getRecentQuantity()>0) {
			stock.setRecentQuantity(stock.getRecentQuantity()-1);
			updateStock(stock);
		}
		
		if(stock.getQuantityMin()!=0) {
			if(stock.getRecentQuantity()==stock.getQuantityMin()) {
				int addedQuantity= stock.getQuantity()-stock.getRecentQuantity();
				ProviderOrder order = new ProviderOrder();
				order.setProduct(product);
				order.setStore(store);
				order.setQuantity(addedQuantity);
				sendJavaMail(order,stock);
			}
		}

		return stock;

	}
	
	public void checkStockDateQuantity(Stock stock) {
		Date dateStock = stock.getCreatedAt();
		Date dateSys=new Date(System.currentTimeMillis());
		System.out.println("date stock = "+dateStock);
		System.out.println("date systeme = "+dateSys);
		long diffInMillies = Math.abs(dateSys.getTime() - dateStock.getTime());
		long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
		System.out.println("diff= "+diff);

		if(diff>30) {
			stock.setQuantityMin(0);
		}
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
	public void updateStockProvider(int idStock, int addedQuantity) {
		Stock stock = findStockById(idStock);
		stock.setRecentQuantity(stock.getRecentQuantity()+addedQuantity);
		stock.setCreatedAt(new Date(System.currentTimeMillis()));

	}

	@Override
	public Stock findStockById(int idStock) {
		Stock s = em.find(Stock.class, idStock);
		return s;
	}






}
