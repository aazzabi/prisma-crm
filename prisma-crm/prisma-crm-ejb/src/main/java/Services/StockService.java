package Services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Local;
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

	@Override
	public String addStock(Stock stock,Store store) {
		int q =calculRecentQuantity(store);

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

		
	}
	@Override
	public void sendJavaMail(ProviderOrder order) {
		try {
			JavaMailUtil.sendMail("provider.prisma@gmail.com", "Products Order",
					"<h3>Products Order</h3>"
					+"<p>Store Name :"+order.getStore().getName()+"</p>"
					+"<p>Product Reference :"+order.getProductRef()+"</p>"
					+"<p>Quantity :"+order.getQuantity()+"</p>"
					+"<a href='http://localhost:9080/prisma-crm-web/stock/add_products?idStore="+order.getStore().getId()+"&&ref="+order.getProductRef()+"&&quantity="+order.getQuantity()+"'>Accept the order</a>"
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

}
