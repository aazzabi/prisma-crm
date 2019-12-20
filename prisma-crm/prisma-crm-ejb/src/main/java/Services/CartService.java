package Services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Entities.Address;
import Entities.CartProductRow;
import Entities.Client;
import Entities.ClientCart;
import Entities.ClientOrder;
import Entities.Invoice;
import Entities.Product;
import Entities.ReductionFidelityRation;
import Entities.Store;
import Entities.TemporaryInvoice;
import Enums.OrderType;
import Interfaces.ICartLocal;
import Utils.Mailer;
import Utils.TimeDistance;
import Utils.TravelInformation;

@Stateless
@LocalBean
public class CartService implements ICartLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	@EJB
	OrderService orderBusiness;
	@EJB
	InvoiceService invoiceBusiness;
	private final String distanceMatrixAPI = "http://www.mapquestapi.com/directions/v2/routematrix";
	private final String distanceMatrixAPITokenKey = "qgluQem4iTGKYyMxdp1MdsyGHnwwFdva";
	private final String REVERSE_GEOCODING_API = "https://nominatim.openstreetmap.org/reverse.php";
	private JsonObject distanceMatrixParams = new JsonObject();

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

	// Tested (Adding product to row)
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
			ROW.setTotalPriceWNReduction(quantity * PRODUCT.getPrice());
			PRODUCT.setStock(PRODUCT.getStock() - quantity);
			ROW.setFinalPrice(quantity * PRODUCT.getPrice());
			manager.merge(PRODUCT);
			// Searching for Reduction Ratio for the product
			ReductionFidelityRation RATION = (ReductionFidelityRation) manager
					.createQuery("SELECT R FROM ReductionFidelityRation R WHERE R.productType =:a")
					.setParameter("a", PRODUCT.getType()).getSingleResult();
			// Calling for the Reduction service to apply reduction
			if ((withReduction) && (RATION != null)) {
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

	// Tested
	@Override
	public Product deleteProductFromCart(int product, int cart) {
		ClientCart CART = manager.find(ClientCart.class, cart);
		Product PRODUCT = manager.find(Product.class, product);
		if ((CART != null) && (PRODUCT != null)) {

			CartProductRow row = (CartProductRow) manager
					.createQuery("SELECT c FROM CartProductRow c " + "where c.cart =:cart AND c.product=:product")
					.setParameter("cart", CART).setParameter("product", PRODUCT).getSingleResult();
			PRODUCT.setStock(PRODUCT.getStock() + row.getQuantity());
			row.getCart().getClient()
					.setFidelityScore(row.getCart().getClient().getFidelityScore() + row.getUsedFidelityPoints());
			manager.merge(PRODUCT);
			manager.merge(row.getCart().getClient());
			manager.remove(row);
			manager.flush();
			return PRODUCT;
		} else
			return null;
	}

	// Tested
	@Override
	public TimeDistance passToCheckOut(OrderType orderType, int client, int cart, double LONG, double LAT) {
		Client customer = manager.find(Client.class, client);
		ClientCart crt = manager.find(ClientCart.class, cart);
		if ((customer != null) && (crt != null)) {
			ClientOrder order = new ClientOrder();
			order.setClient(customer);
			java.util.Date date = new java.util.Date();
			order.setCreatedAt(new Date(date.getTime()));
			order.setClient(customer);
			order.setValid(false);
			order.setReductionRatio(calculateOrderReductionRation(crt.getId()));
			order.setCart(crt);
			crt.setUpdatedAt(new Timestamp(date.getTime()));
			order.setOrderNature(orderType);
			manager.persist(order);
			Double total = crt.getCartRows().stream().filter(o -> o.getFinalPrice() > 0)
					.mapToDouble(o -> o.getFinalPrice()).sum();
			order.setTotale(total.floatValue());
			if (order.getOrderNature() == OrderType.LocalPaymentMethod) {
				java.util.Date dt = new java.util.Date();
				Calendar c = Calendar.getInstance();
				c.setTime(dt);
				c.add(Calendar.DATE, 1);
				dt = c.getTime();
				TemporaryInvoice tmpInvoice = new TemporaryInvoice();
				tmpInvoice.setCreatedAt(new Date(date.getTime()));
				tmpInvoice.setDeadline(new Date(dt.getTime()));
				tmpInvoice.setOrder(order);
				manager.persist(tmpInvoice);
			} else {
				// Invocation of paypal
				orderBusiness.checkOutOrder(order);
				order.setValid(true);
				// Creation of the invoice and send it to mail
				manager.persist(order);
				manager.flush();
				invoiceBusiness.createInvoice(order.getId());
			}
			crt.setOrder(order);
			manager.merge(crt);
			manager.flush();
			TimeDistance distannce = getTimeNeededToGetOrderProducts(LONG, LAT, crt.getId());
			return distannce != null ? distannce : null;
		}

		else
			return null;
	}

	public boolean sendCartReminder(ClientCart cart) {

		try {
			Mailer.sendAsHtml(cart.getClient().getEmail(), "Forgotten cart", "<p>Test mail</p>", null);
			return true;
		} catch (MessagingException m) {
			m.printStackTrace();
			return false;
		}

	}

	// Tested
	@SuppressWarnings("unchecked")
	@Override
	public List<ClientCart> fetchCarts() {

		return manager.createQuery("SELECT C FROM ClientCart C ").getResultList();
	}

	// Tested
	@SuppressWarnings("unchecked")
	@Override
	public List<ClientCart> getClientCarts(int clientid) {
		Client client = manager.find(Client.class, clientid);
		return client != null
				? manager.createQuery("SELECT C FROM ClientCart C WHERE C.client=:client and C.isCheckedOut=false")
						.setParameter("client", client).getResultList()
				: null;

	}

	// Tested
	@SuppressWarnings("unchecked")
	@Override
	public List<ClientCart> searchForClientCarts(String value, String criteria) {
		return manager.createQuery("SELECT C FROM ClientCart C WHERE C." + criteria + "=:value")
				.setParameter("value", value).getResultList();

	}

	// Tested (applied for each ClientProductRow)
	public boolean applyCartReduction(CartProductRow cart, int desiredFidelityPoints) {
		if (cart.getCart().getClient().getFidelityScore() >= desiredFidelityPoints) {
			cart.setUsedFidelityPoints(desiredFidelityPoints);
			// Calculating the sum of reduction
			float REDUCTION_AMMOUNT_PER_UNIT = (desiredFidelityPoints
					/ cart.getReductionRatio().getFidelityScoreForEach())
					* (cart.getReductionRatio().getReductionRatio());
			// Updating final sum
			cart.setFinalPrice(cart.getFinalPrice() - (REDUCTION_AMMOUNT_PER_UNIT));
			cart.getCart().getClient()
					.setFidelityScore(cart.getCart().getClient().getFidelityScore() - desiredFidelityPoints);
			manager.persist(cart);
			manager.merge(cart.getCart().getClient());
			manager.flush();
			return true;
		} else
			return false;

	}

	// Invoked while passing to checkout
	public Store getNearestStoreAddress(double LON, double LAT) {
		// Fetching nearest store to the user
		List<Store> stores = manager.createQuery("SELECT S FROM Store S", Store.class).getResultList();
		Store nearStore = stores.get(0);
		float min = calculateDistanceBetweenTwoStores(nearStore.getAddress().getLongtitude(),
				nearStore.getAddress().getLatitude(), LON, LAT);
		if (stores.size() > 0) {
			for (Store s : stores) {
				float result = calculateDistanceBetweenTwoStores(s.getAddress().getLongtitude(),
						s.getAddress().getLatitude(), LON, LAT);
				if (result < min) {
					min = result;
					nearStore = s;
				}
			}
		}
		writeLog(
				"Beginning of the CLIENT LINKING TO THE STORE PROCESS \n \n \n----------------------------------------------\nNeares store is "
						+ nearStore.getName() + "\n\n");
		return nearStore;
	}

	// Tested
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

	public TimeDistance getTimeNeededToGetOrderProducts(double LONG, double LAT, int cartId) {
		// Getting the nearest store to client
		ClientCart cart = manager.find(ClientCart.class, cartId);
		Store nearest = getNearestStoreAddress(LONG, LAT);
		if (nearest != null) {
			// reverse Geocoding client
			Address clientCurrentAddress = reverseGeoCode(LONG, LAT);
			// Calculating time between client and store : tested
			TimeDistance CLIENT_STORE_DISTANCE = getDistanceTimeFromOriginToDestination(
					clientCurrentAddress.getDisplayName(), nearest.getAddress().getDisplayName());
			// Calculating distance between stores ...
			List<Store> includedStores = fetchingCartIncludedStores(cart);
			if (includedStores.size() > 0) {
				TimeDistance MAX_DISTANCE_STORE_STORE = new TimeDistance();
				MAX_DISTANCE_STORE_STORE.setDistance(0);
				MAX_DISTANCE_STORE_STORE.setTime(0);
				for (Store tmp : includedStores) {
					TimeDistance timeDistance = getDistanceTimeFromOriginToDestination(
							nearest.getAddress().getDisplayName(), tmp.getAddress().getDisplayName());
					writeLog("\n\n*****Time distance object between " + tmp.getAddress().getDisplayName() + " And "
							+ nearest.getAddress().getDisplayName() + " is " + timeDistance.getDistance()
							+ " KM and time : is " + timeDistance.getTime() + "\n");
					if (timeDistance.getTime() > MAX_DISTANCE_STORE_STORE.getTime()) {
						MAX_DISTANCE_STORE_STORE = timeDistance;
					}
				}
				writeLog("End of the process\n\n\n------------------------------------------------");
				if (MAX_DISTANCE_STORE_STORE != null) {
					return MAX_DISTANCE_STORE_STORE.getTime() > CLIENT_STORE_DISTANCE.getTime()
							? MAX_DISTANCE_STORE_STORE
							: CLIENT_STORE_DISTANCE;
				} else
					return null;
			} else
				return null;

		}

		else
			return null;

	}

// Tested
	public Address reverseGeoCode(double LON, double LAT) {
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		WebTarget target = client.target(REVERSE_GEOCODING_API).queryParam("lat", LAT).queryParam("lon", LON)
				.queryParam("format", "json").queryParam("accept-language", "fr");
		Response response = target.request().get();
		String result = response.readEntity(String.class);
		JsonParser parser = new JsonParser();
		JsonObject DISPLAY_NAME = (JsonObject) parser.parse(result);
		client.close();
		Address address = new Address();
		address.setLongtitude(DISPLAY_NAME.get("lon").getAsFloat());
		address.setLatitude(DISPLAY_NAME.get("lat").getAsDouble());
		address.setDisplayName(DISPLAY_NAME.get("display_name").getAsString());
		JsonObject country = DISPLAY_NAME.get("address").getAsJsonObject();
		address.setCountry(country.get("country").getAsString());
		address.setZipCode(11);
		writeLog("\n////Address Longtitude : " + address.getLongtitude());
		writeLog("\n////Address Latitude : " + address.getLatitude());
		writeLog("\n////Address displayName : " + address.getDisplayName());
		return address;
	}

	// Tested
	public TimeDistance getDistanceTimeFromOriginToDestination(String a, String b) {
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		WebTarget target = client.target(distanceMatrixAPI).queryParam("key", distanceMatrixAPITokenKey);
		JsonArray locations = new JsonArray();
		locations.add(a);
		locations.add(b);
		distanceMatrixParams.add("locations", locations);
		distanceMatrixParams.addProperty("allToAll", false);
		Response response = target.request().post(Entity.text(distanceMatrixParams));
		String responseStr = response.readEntity(String.class);
		client.close();
		TimeDistance PACKAGE_RESULT = new TimeDistance();
		JsonParser parser = new JsonParser();
		JsonObject JSON_RESPONSE_RESULT = (JsonObject) parser.parse(responseStr);
		if (JSON_RESPONSE_RESULT.get("distance") != null) {
			JsonArray DISTANCE_ARRAY = JSON_RESPONSE_RESULT.get("distance").getAsJsonArray();
			JsonArray TIME_ARRAY = JSON_RESPONSE_RESULT.get("time").getAsJsonArray();
			PACKAGE_RESULT.setDistance(DISTANCE_ARRAY.get(1).getAsFloat());
			PACKAGE_RESULT.setTime(TIME_ARRAY.get(1).getAsFloat());
			java.util.Date date = new java.util.Date();
			return PACKAGE_RESULT;
		} else
			return null;
	}

	// Tested
	public List<Store> fetchingCartIncludedStores(ClientCart cart) {
		writeLog("Fetching stores which they are included in the sale : ");
		List<Store> stores = new ArrayList<Store>();
		// Fetching rows :
		Set<CartProductRow> rows = cart.getCartRows();
		for (CartProductRow row : rows) {
			if (!stores.contains(row.getProduct().getStore())) {
				writeLog("Store : " + row.getProduct().getStore().getName() + "\n");
				stores.add(row.getProduct().getStore());
			}
		}
		return stores;

	}

	// Tested
	public static void writeLog(String info) {
		String filename = "activity.log";
		String FILENAME = "C:\\Users\\samal\\git\\prisma-crm\\" + filename;
		BufferedWriter bw = null;
		FileWriter fw = null;
		try {
			fw = new FileWriter(FILENAME, true);
			bw = new BufferedWriter(fw);
			bw.write(info);
			bw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bw != null)
					bw.close();
				if (fw != null)
					fw.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Tested
	public ClientCart getCart(int id) {
		return manager.find(ClientCart.class, id);
	}

	// Tested
	public float calculateOrderReductionRation(int cartId) {
		ClientCart cart = manager.find(ClientCart.class, cartId);
		if (cart != null) {
			Double totalWNR = cart.getCartRows().stream().filter(o -> o.getTotalPriceWNReduction() > 0)
					.mapToDouble(o -> o.getTotalPriceWNReduction()).sum();
			Double totalWR = cart.getCartRows().stream().filter(o -> o.getFinalPrice() > 0)
					.mapToDouble(o -> o.getFinalPrice()).sum();
			return (float) (1 - (totalWR / totalWNR));
		} else
			return 0;
	}
	// Needs the update of the cart update and cancel services .
	// For admin : need to implement statistics for carts {group carts of them by
	// clients , group carts by type and status ,
	// most added products to cart.}

	@Override
	public Product updateProductCartRow(int product, int cart, int quantity, int points, boolean withReduction) {
		Product PRODUCT = manager.find(Product.class, product);
		ClientCart CART = manager.find(ClientCart.class, cart);
		if ((PRODUCT != null) && (CART != null) && (checkIfProductIsContainedInCart(CART, PRODUCT))
				&& (PRODUCT.getStock() >= quantity)) {
			CartProductRow row = (CartProductRow) manager
					.createQuery("SELECT c FROM CartProductRow c " + "where c.cart =:cart AND c.product=:product")
					.setParameter("cart", CART).setParameter("product", PRODUCT).getSingleResult();
			row.setQuantity(quantity);
			row.setTotalPriceWNReduction(quantity * PRODUCT.getPrice());
			row.setFinalPrice(quantity * PRODUCT.getPrice());
			if ((CART.getClient().getFidelityScore() >= points) && (withReduction)) {
				row.setUsedFidelityPoints(points);
				// Calculating the sum of reduction
				float REDUCTION_AMMOUNT_PER_UNIT = (points / row.getReductionRatio().getFidelityScoreForEach())
						* (row.getReductionRatio().getReductionRatio());
				// Updating final sum
				row.setFinalPrice(row.getFinalPrice() - (REDUCTION_AMMOUNT_PER_UNIT));
				row.getCart().getClient().setFidelityScore(row.getCart().getClient().getFidelityScore() - points);
			} else {
				row.setFinalPrice(row.getFinalPrice());
				row.setUsedFidelityPoints(0);
			}
			PRODUCT.setStock(PRODUCT.getStock() - row.getQuantity());
			manager.merge(row.getCart().getClient());
			manager.merge(PRODUCT);
			manager.merge(row);
			manager.flush();
			return PRODUCT;
		}
		return null;
	}

	public boolean checkIfProductIsContainedInCart(ClientCart cart, Product product) {
		for (CartProductRow p : cart.getCartRows()) {
			if (p.getProduct().getId() == product.getId())
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Product> fetchProductsFromCart() {
		return manager.createQuery("SELECT p from Product P where P.stock>0 ").getResultList();
	}

	@Override
	public Product getProductById(int id) {
		Product p = manager.find(Product.class, id);
		return p;
	}

	@Override
	public ReductionFidelityRation getProductReductionRatio(int productId) {
		Product p = manager.find(Product.class, productId);
		if (p != null) {
			ReductionFidelityRation r = (ReductionFidelityRation) manager
					.createQuery("SELECT R FROM ReductionFidelityRation R" + " where  R.productType=:a")
					.setParameter("a", p.getType()).getSingleResult();
			return r != null ? r : null;
		} else
			return null;
	}

	@Override
	public CartProductRow getProductCartRow(int productId, int cartId) {
		Product p = manager.find(Product.class, productId);
		ClientCart c = manager.find(ClientCart.class, cartId);
		if ((p != null) && (c != null)) {
			try {
				CartProductRow row = (CartProductRow) manager
						.createQuery("SELECT R FROM CartProductRow R WHERE R.product=:product and R.cart=:cart")
						.setParameter("product", p).setParameter("cart", c).getSingleResult();

				System.out.println(row);
				if (row != null) {
					return row;
				} else
					return null;
			}

			catch (NoResultException e) {
				return null;
			}
		} else
			return null;
	}

	@Override
	public CartProductRow resetUserPoints(int clientId, int points, int productQuantity, int productId, int cartId) {
		Client client = manager.find(Client.class, clientId);
		Product product = manager.find(Product.class, productId);
		if ((client != null) && (product != null)) {
			client.setFidelityScore(client.getFidelityScore() + points);
			product.setStock(product.getStock() + productQuantity);
			return getProductCartRow(productId, cartId);

		} else
			return null;

	}

	@Override
	public Set<CartProductRow> getCartRows(int cartId) {

		return manager.find(ClientCart.class, cartId).getCartRows();
	}

	@Override
	public float passToCheckOutUsingCash(int cartId, int clientId, double distanceCS, int storeId) {
		Store store = manager.find(Store.class, storeId);
		ClientCart cart = manager.find(ClientCart.class, cartId);
		Client client = manager.find(Client.class, clientId);
		if (client != null && cart != null && store != null) {
			ClientOrder order = new ClientOrder();
			order.setCart(cart);
			order.setClient(client);
			java.util.Date date = new java.util.Date();
			order.setCreatedAt(new Date(date.getTime()));
			order.setOrderNature(OrderType.LocalPaymentMethod);
			order.setStore(store);
			order.setValid(false);
			order.setTotale((float) (cart.getCartRows().stream().filter(o -> o.getFinalPrice() > 0)
					.mapToDouble(o -> o.getFinalPrice())).sum());
			Double totalWNR = cart.getCartRows().stream().filter(o -> o.getTotalPriceWNReduction() > 0)
					.mapToDouble(o -> o.getTotalPriceWNReduction()).sum();
			order.setReductionRatio((float) (order.getTotale() / totalWNR));
			cart.setCheckedOut(true);
			manager.persist(order);
			List<Store> includedStores = fetchingCartIncludedStores(cart);
			// getting max distance between stores
			if (includedStores != null && includedStores.size() > 0) {
				float maxDistance = 0;
				for (Store s : includedStores) {
					float tmp = calculateDistanceBetweenTwoStores(s.getAddress().getLongtitude(),
							s.getAddress().getLatitude(), store.getAddress().getLongtitude(),
							store.getAddress().getLatitude());
					if (tmp > maxDistance) {
						maxDistance = tmp;
					}
				}
				// creating temporary invoice and linking it to the order
				TemporaryInvoice invoice = new TemporaryInvoice();
				invoice.setCreatedAt(new Date(date.getTime()));
				invoice.setDeadline(new Date(date.getTime() + (24 * 3600)));
				invoice.setOrder(order);
				manager.persist(invoice);
				manager.merge(cart);
				manager.flush();
				if (maxDistance > distanceCS) {
					return maxDistance;
				} else
					return (float) distanceCS;
			} else
				return (float) distanceCS;
		} else
			return 0;
	}


	@Override
	public float passToCheckOutUsingPayPal(int cartId, int clientId, double distanceCS, int storeId) {
		ClientCart cart = manager.find(ClientCart.class, cartId);
		Client client = manager.find(Client.class, clientId);
		Store store=manager.find(Store.class, storeId);
		if (client != null && cart != null && store != null) {
			ClientOrder order = new ClientOrder();
			order.setCart(cart);
			order.setClient(client);
			java.util.Date date = new java.util.Date();
			order.setCreatedAt(new Date(date.getTime()));
			order.setOrderNature(OrderType.OnlinePaymentMethod);
			order.setStore(store);
			order.setValid(true);
			order.setTotale((float) (cart.getCartRows().stream().filter(o -> o.getFinalPrice() > 0)
					.mapToDouble(o -> o.getFinalPrice())).sum());
			Double totalWNR = cart.getCartRows().stream().filter(o -> o.getTotalPriceWNReduction() > 0)
					.mapToDouble(o -> o.getTotalPriceWNReduction()).sum();
			order.setReductionRatio((float) (order.getTotale() / totalWNR));
			manager.persist(order);
			List<Store> includedStores = fetchingCartIncludedStores(cart);
			// getting max distance between stores
			if (includedStores != null && includedStores.size() > 0) {
				float maxDistance = 0;
				for (Store s : includedStores) {
					float tmp = calculateDistanceBetweenTwoStores(s.getAddress().getLongtitude(),
							s.getAddress().getLatitude(), store.getAddress().getLongtitude(),
							store.getAddress().getLatitude());
					if (tmp > maxDistance) {
						maxDistance = tmp;
					}
				}
				// creating permanent invoice and linking it to the order
				Invoice invoice = new Invoice();
				invoice.setOrder(order);
				invoice.setCreatedAt(new Timestamp(date.getTime()));
				invoice.setOrderInvoice(order);
				cart.setOrder(order);
				cart.setCheckedOut(true);
				order.setCart(cart);
				order.setInvoice(invoice);
				manager.persist(order);
				manager.merge(cart);
				manager.persist(invoice);
				manager.flush();
				if (maxDistance > distanceCS) {
					return maxDistance;
				} else
					return (float) distanceCS;
			} else
				return (float) distanceCS;
		} else
			return 0;
	}

	@Override
	public void deleteCart(int cart) {
		ClientCart c = manager.find(ClientCart.class, cart);
		if (c.getCartRows() != null && c.getCartRows().size() > 0) {
			for (CartProductRow p : c.getCartRows()) {
				manager.remove(p);
				manager.flush();
			}
			manager.remove(c);
			manager.flush();
		} else {
			manager.remove(c);
			manager.flush();
		}
	}
	
	/*
	 Implementing methods to fetch client orders & quotations
	 */
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ClientOrder> getClientOrders(int id)
	{
		Client c=manager.find(Client.class, id);
		if (c!=null)
		{
			List<ClientOrder> orders=manager.createQuery("SELECT O FROM ClientOrder O WHERE O.client=:client")
									 .setParameter("client", c)
									 .getResultList();
			return orders;
									
		}
		return null;
	}

	@Override
	public ClientCart getCartById(int id) {
		
		return manager.find(ClientCart.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Set<CartProductRow> getOrderRows(int id) {
			ClientOrder cart=manager.find(ClientOrder.class, id);
			if (cart!=null)
			{
				if (cart.getCart().getCartRows()!=null)
				{
					return cart.getCart().getCartRows();
				}
				else return null;
			}
			else return null;
	}

	@Override
	public ClientOrder getSpecificClientOrder(int id) {
		return manager.find(ClientOrder.class,id);
	}

}
