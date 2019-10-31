package Services;



import java.sql.Date;
import java.sql.Timestamp;
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
	public ClientCart createCart(ClientCart cart, int client) {
		Client cl = manager.find(Client.class, client);
		if (cl != null) {
			cart.setClient(cl);
			manager.persist(cart);
			manager.flush();
			return cart;
		} else
			return null;
	}

	@Override
	public Product addProductToCart(int product, int cart, int quantity, int points, boolean withReduction) {
		Product PRODUCT = manager.find(Product.class, product);
		ClientCart CART = manager.find(ClientCart.class, cart);
		if ((PRODUCT != null) && (CART != null) && (PRODUCT.getStock() >= quantity)) {
			// Initializing ROW
			CartProductRow ROW = new CartProductRow();
			ROW.setCart(CART);
			ROW.setOriginalPrice(PRODUCT.getPrice());
			ROW.setQuantity(quantity);
			ROW.setProduct(PRODUCT);
			PRODUCT.setStock(PRODUCT.getStock() - quantity);
			ROW.setFinalPrice(quantity*PRODUCT.getPrice());
			manager.merge(PRODUCT);
			// Searching for Reduction Ratio for the product
			ReductionFidelityRation RATION = (ReductionFidelityRation) manager.createQuery(
					"SELECT R FROM ReductionFidelityRation R WHERE R.productType =:a")
					.setParameter("a", PRODUCT.getType())
					.getSingleResult();
			// Calling for the Reduction service to apply reduction
			if ((withReduction) && (RATION!=null)) {
				ROW.setReductionRatio(RATION);
				if (applyCartReduction(ROW, points)) {
					return PRODUCT;
				} else {
					manager.persist(ROW);
					return PRODUCT;
				}

			} else {
				manager.persist(ROW);
				return PRODUCT;
			}

		} else
			return null;

	}

	@Override
	public Product deleteProductFromCart(int product, int cart) {
		ClientCart CART = manager.find(ClientCart.class, cart);
		Product PRODUCT = manager.find(Product.class, product);
		if ((CART != null) && (PRODUCT != null)) {

			CartProductRow row = (CartProductRow) manager
					.createQuery("SELECT c FROM CartProductRow c " + "where c.cart =:cart AND c.product=:product")
					.setParameter("cart", CART).setParameter("product", PRODUCT).getSingleResult();
			PRODUCT.setStock(PRODUCT.getStock()+row.getQuantity());
			row.getCart().getClient().setFidelityScore(row.getCart().getClient().getFidelityScore()+row.getUsedFidelityPoints());
			manager.merge(PRODUCT);
			manager.merge(row.getCart().getClient());
			manager.remove(row);
			manager.flush();
			return PRODUCT;
		} else
			return null;
	}

	@Override
	public ClientOrder passToCheckOut(OrderType orderType, int client, int cart) {
		Client customer = manager.find(Client.class, client);
		ClientCart crt = manager.find(ClientCart.class, cart);
		if ((customer != null) && (crt != null)) {
			ClientOrder order = new ClientOrder();
			order.setClient(customer);
			java.util.Date date = new java.util.Date();
			order.setCreatedAt(new Date(date.getTime()));
			order.setOrderNature(orderType);
			order.setClient(customer);
			order.setValid(false);
			order.setCart(crt);
			crt.setUpdatedAt(new Timestamp(date.getTime()));
			crt.setOrder(order);
			Double total=crt.getCartRows().stream().filter(o->o.getFinalPrice()>0).mapToDouble(o->o.getFinalPrice()).sum();
			order.setTotale(total.floatValue());
			manager.persist(order);
			manager.flush();
			manager.merge(crt);
			manager.flush();
			return order;
		}

		else return null;
	}

	@Override
	public boolean sendCartReminder(ClientCart cart) {
		try {
			Mailer.sendAsHtml(cart.getClient().getEmail(), "Forgotten cart", "<p>Test mail</p>", cart.getClient());
			return true;
		} catch (MessagingException m) {
			m.printStackTrace();
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientCart> fetchCarts() {

		return manager.createQuery("SELECT C FROM ClientCart C ").getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientCart> getClientCarts(int clientid) {
		Client client = manager.find(Client.class, clientid);
		return client!=null?manager.createQuery("SELECT C FROM ClientCart C WHERE C.client=:client")
								   .setParameter("client",client)
								   .getResultList()
							:null;	   

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientCart> searchForClientCarts(String value, String criteria) {
		return manager.createQuery("SELECT C FROM ClientCart C WHERE C." + criteria + "=:value")
				.setParameter("value", value).getResultList();

	}

	public boolean applyCartReduction(CartProductRow cart, int desiredFidelityPoints) {
		if (cart.getCart().getClient().getFidelityScore() >= desiredFidelityPoints) {
			cart.setUsedFidelityPoints(desiredFidelityPoints);
			// Calculating the sum of reduction
			float REDUCTION_AMMOUNT_PER_UNIT = (desiredFidelityPoints
					/ cart.getReductionRatio().getFidelityScoreForEach())
					* (cart.getReductionRatio().getReductionRatio());
			// Updating final sum
			cart.setFinalPrice(cart.getFinalPrice() - (cart.getQuantity() * REDUCTION_AMMOUNT_PER_UNIT));
			cart.getCart().getClient()
					.setFidelityScore(cart.getCart().getClient().getFidelityScore() - desiredFidelityPoints);
			manager.persist(cart);
			manager.merge(cart.getCart().getClient());
			manager.flush();
			return true;
		} else
			return false;

	}

}
