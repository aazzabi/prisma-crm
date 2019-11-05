package Services;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
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
import Entities.Product;
import Entities.ReductionFidelityRation;
import Entities.Store;
import Enums.OrderType;
import Interfaces.ICartLocal;
import Utils.Mailer;
import Utils.TimeDistance;

@Stateless
@LocalBean
public class CartService implements ICartLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	@EJB
	OrderService orderBusiness;
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
			Double total = crt.getCartRows().stream().filter(o -> o.getFinalPrice() > 0)
					.mapToDouble(o -> o.getFinalPrice()).sum();
			order.setTotale(total.floatValue());
			manager.persist(order);
			crt.setOrder(order);
			manager.merge(crt);
			manager.flush();
			return getTimeNeededToGetOrderProducts(LONG, LAT, crt.getId());
		}

		else
			return null;
	}

	// Cron job (not tested)
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
				? manager.createQuery("SELECT C FROM ClientCart C WHERE C.client=:client")
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

//Still in a whole mess ...
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
				return MAX_DISTANCE_STORE_STORE.getTime() > CLIENT_STORE_DISTANCE.getTime() ? MAX_DISTANCE_STORE_STORE
						: CLIENT_STORE_DISTANCE;
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
		address.setZipCode(country.get("postcode").getAsInt());
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
		JsonArray DISTANCE_ARRAY = JSON_RESPONSE_RESULT.get("distance").getAsJsonArray();
		JsonArray TIME_ARRAY = JSON_RESPONSE_RESULT.get("time").getAsJsonArray();
		PACKAGE_RESULT.setDistance(DISTANCE_ARRAY.get(1).getAsFloat());
		PACKAGE_RESULT.setTime(TIME_ARRAY.get(1).getAsFloat());
		java.util.Date date = new java.util.Date();
		writeLog(new Timestamp(date.getTime()).toString() + "\n"
				+ "----------------------------------------------------\n" + "distance between " + a + " || and ||" + b
				+ " is " + PACKAGE_RESULT.getDistance() + "\nTime : " + PACKAGE_RESULT.getTime());
		return PACKAGE_RESULT;
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
		String FILENAME = "C:\\Users\\samali\\git\\" + filename;
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
	// For admin : need to implement statistics for carts  {group carts of them by clients , group carts by type and status ,
	//  most added products to cart.}
	

}
