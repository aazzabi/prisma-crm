package Services;

import java.sql.Date;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.CartProductRow;
import Entities.Client;
import Entities.ClientCart;
import Entities.ClientOrder;
import Entities.Product;
import Entities.ReductionFidelityRation;
import Enums.OrderType;
import Interfaces.ICartLocal;
import Utils.Mailer;

@Stateless
@LocalBean
public class CartService implements ICartLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	@EJB
	OrderService orderBusiness;
	
	@Override
	public ClientCart createCart(ClientCart cart,int client) {
		Client cl=manager.find(Client.class, client);
		if (cl!=null)
		{
		cart.setClient(cl);	
		manager.persist(cart);
		manager.flush();
		return cart;
		}
		else 
		return null;
	}

	@Override
	public Product addProductToCart(int product, int cart, int quantity,int points) {
		Product pr=manager.find(Product.class, product);
		ClientCart crt=manager.find(ClientCart.class, cart);
		if((crt!=null)
				&& (pr!=null))
		{
			CartProductRow row=new CartProductRow();
			row.setCart(manager.find(ClientCart.class, cart));
			row.setProduct(manager.find(Product.class, product));
			row.setOriginalPrice(pr.getPrice());
			row.setFinalPrice(pr.getPrice()*quantity);
			ReductionFidelityRation ratio=(ReductionFidelityRation)manager.createQuery("SELECT R FROM ReductionFidelityRation"
														   + " R where R.productType=:producttype")
															 .setParameter(":producttype", pr.getType());
			row.setReductionRatio(ratio);
			if (applyCartReduction(row, points))
				return pr;
			else
			manager.persist(row);
			manager.flush();
			return pr;
		}
		else
		return null;
	}

	@Override
	public Product deleteProductFromCart(int product, int cart) {
		if((manager.find(Product.class, product)!=null)
				&& (manager.find(ClientCart.class, cart)!=null))
		{
		ClientCart crt=manager.find(ClientCart.class,cart);
		Product prdct=manager.find(Product.class,product);
		CartProductRow row=(CartProductRow)manager.createQuery("SELECT c FROM CartProductRow c "
											 + "where c.cart =:cart AND c.product=:product")
								                  .setParameter("cart",crt)
								                  .setParameter("product",prdct)
								                  .getSingleResult();
		manager.remove(row);
		manager.flush();
		return prdct;
		}
		else return null;
	}

	@Override
	public ClientOrder passToCheckOut(OrderType orderType,int client,int cart) {
		Client customer=manager.find(Client.class, client);
		ClientCart crt=manager.find(ClientCart.class, cart);
		if ((customer!=null) && (crt!=null))
		{
			ClientOrder order=new ClientOrder();
			order.setClient(customer);
			java.util.Date date=new java.util.Date();
			order.setCreatedAt(new Date(date.getTime()));
			order.setOrderNature(orderType);
			order.setClient(customer);
			order.setValid(false);
			crt.setOrder(order);
			orderBusiness.addOrder(customer.getId(), order, crt);
			manager.merge(crt);
			manager.flush();
		}
				
		return null;
	}

	@Override
	public boolean sendCartReminder(ClientCart cart) {
		try {
		Mailer.sendAsHtml(cart.getClient().getEmail(), "Forgotten cart","<p>Test mail</p>", cart.getClient());
		return true;
		}
		catch (MessagingException m)
		{
			m.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientCart> fetchCarts() {
		
		return manager.createQuery("SELECT C FROM ClientCart C ").getResultList();
	}

	@Override
	public ClientCart getClientCart(int id,int cartid) {
		Client client=manager.find(Client.class,id);
		ClientCart cart=manager.find(ClientCart.class,cartid);
		if ((client!=null) && (cart!=null))
		{
			return(ClientCart) manager.createQuery("SELECT C FROM ClientCart C WHERE C.client=:client")
					      			  .setParameter("client",client)
					      			  .getSingleResult();
		}
		else {
			return null;
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientCart> searchForClientCarts(String value, String criteria) {
		return manager.createQuery("SELECT C FROM ClientCart C WHERE C."+criteria+"=:value")
    			  .setParameter("value",value)
    			  .getResultList();

	}

	
/*This web service is for time cost from point a to b : 
 * http://www.mapquestapi.com/directions/v2/routematrix?key=qgluQem4iTGKYyMxdp1MdsyGHnwwFdva
*/

	public boolean applyCartReduction(CartProductRow cart,int desiredFidelityPoints) {
		if ((cart!=null) && (desiredFidelityPoints<=cart.getCart().getClient().getFidelityScore()))
		{
			float finalRatio=((int)desiredFidelityPoints/cart.
					getReductionRatio().getFidelityScoreForEach())
					*(cart.getReductionRatio().getReductionRatio());
			cart.setFinalPrice((cart.getFinalPrice())-(finalRatio*cart.getQuantity()));
			manager.persist(cart);
			manager.flush();
			return true;
		
		}
		
		else return false;
	}
	
	
	
	

}
