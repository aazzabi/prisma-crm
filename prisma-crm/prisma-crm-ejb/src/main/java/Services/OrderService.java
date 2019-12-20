package Services;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Authorization;
import com.paypal.api.payments.Details;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

import Entities.Agent;
import Entities.CartProductRow;
import Entities.Client;
import Entities.ClientOrder;
import Entities.Invoice;
import Entities.Product;
import Enums.OrderType;
import Interfaces.IOrderLocal;

@Stateless
@LocalBean
public class OrderService implements IOrderLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	private final String distanceMatrixAPI = "http://www.mapquestapi.com/directions/v2/routematrix";
	private final String distanceMatrixAPITokenKey = "qgluQem4iTGKYyMxdp1MdsyGHnwwFdva";
	private static String crunchifyID = "AT2A3K3LpexZ8DK_LLEd0ozXd4i7AFh9kTEHWRF3puoW-gIjJFH_yt8rSax0fwphlkPNaIxeRV7bT_Bh";
	private static String crunchifySecret = "EF4_htHLphBnmDoFQrwgEBk0GnAQoyNbuHES5AhZGV-arfAwaQfWchFek62xJG_Hd2equz0oikg-IWzN";
	private static String executionMode = "sandbox"; // sandbox or production
	private JsonObject distanceMatrixParams = new JsonObject();

	public JsonObject getDistanceMatrixAPIParams() {
		return distanceMatrixParams;
	}

	public void setDistanceMatrixAPIParams(JsonObject distanceMatrixAPIParams) {
		this.distanceMatrixParams = distanceMatrixAPIParams;
	}

	public String getDistanceMatrixAPI() {
		return distanceMatrixAPI;
	}

	public String getDistanceMatrixAPITokenKey() {
		return distanceMatrixAPITokenKey;
	}

	@SuppressWarnings("unchecked")
	@Override
	public java.util.List<ClientOrder> fetchOrders() {
		return manager.createQuery("SELECT e FROM ClientOrder e").getResultList();
	}

	@Override
	public ClientOrder getOrder(int id) {
		return manager.find(ClientOrder.class, id);
	}
	// Instead of implementing update order , we need to make cron job to delete
	// orders who's been forgotten after temporaryInvoice deadline

	@Override
	public boolean deleteClientOrder(int id) {
		ClientOrder order = manager.find(ClientOrder.class, id);
		if (order != null) {
			manager.remove(order);
			return true;
		} else
			return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientOrder> searchForOrders(String criteria, String value) {
		return manager.createQuery("SELECT e FROM ClientOrder e where e." + criteria + " LIKE  '" + value + "%'")
				.getResultList();
	}

	// Paypal needs to be debugged
	@Override
	public void checkOutOrder(ClientOrder order) {
		Payer crunchifyPayer = new Payer();
		crunchifyPayer.setPaymentMethod("paypal");

		// Redirect URLs
		RedirectUrls crunchifyRedirectUrls = new RedirectUrls();
		crunchifyRedirectUrls.setCancelUrl("http://localhost:9080/crunchifyCancel");
		crunchifyRedirectUrls.setReturnUrl("http://localhost:9080/crunchifyReturn");

		// Set Payment Details Object
		Details crunchifyDetails = new Details();
		crunchifyDetails.setShipping("2.22");
		crunchifyDetails.setSubtotal("3.33");
		crunchifyDetails.setTax("1.11");

		// Set Payment amount
		Amount crunchifyAmount = new Amount();
		crunchifyAmount.setCurrency("TND");
		crunchifyAmount.setTotal(Float.toString(order.getTotale()));
		crunchifyAmount.setDetails(crunchifyDetails);

		// Set Transaction information
		Transaction crunchifyTransaction = new Transaction();
		crunchifyTransaction.setAmount(crunchifyAmount);
		crunchifyTransaction.setDescription("Client paypal integration demo");
		List<Transaction> crunchifyTransactions = new ArrayList<Transaction>();
		crunchifyTransactions.add(crunchifyTransaction);

		// Add Payment details
		Payment crunchifyPayment = new Payment();

		// Set Payment intent to authorize
		crunchifyPayment.setIntent("authorize");
		crunchifyPayment.setPayer(crunchifyPayer);
		crunchifyPayment.setTransactions(crunchifyTransactions);
		crunchifyPayment.setRedirectUrls(crunchifyRedirectUrls);

		// Pass the clientID, secret and mode. The easiest, and most widely used option.
		APIContext crunchifyapiContext = new APIContext(crunchifyID, crunchifySecret, executionMode);

		try {

			Payment myPayment = crunchifyPayment.create(crunchifyapiContext);

			System.out.println("createdPayment Obejct Details ==> " + myPayment.toString());

			// Identifier of the payment resource created
			crunchifyPayment.setId(myPayment.getId());

			PaymentExecution crunchifyPaymentExecution = new PaymentExecution();

			// Set your PayerID. The ID of the Payer, passed in the `return_url` by PayPal.
			crunchifyPaymentExecution.setPayerId("RQ9JHZCJ5ZGPE");

			// This call will fail as user has to access Payment on UI. Programmatically
			// there is no way you can get Payer's consent.
			Payment createdAuthPayment = crunchifyPayment.execute(crunchifyapiContext, crunchifyPaymentExecution);
			// Transactional details including the amount and item details.
			Authorization crunchifyAuthorization = createdAuthPayment.getTransactions().get(0).getRelatedResources()
					.get(0).getAuthorization();

			System.out.println("Here is your Authorization ID" + crunchifyAuthorization.getId());

		} catch (PayPalRESTException e) {

			System.err.println(e.getDetails());
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientOrder> fetchAllClientOrders(Client client) {
		if (manager.find(Client.class, client.getId()) != null) {
			return manager.createQuery("SELECT c FROM ClientOrder where c.client =:client")
					.setParameter("client", client).getResultList();
		} else
			return null;
	}

	// This service is invoked after linking the client with a certain store
	@Override
	public String calculateDistanceBetweenClientAndStore(ClientOrder order, String origin) {
		javax.ws.rs.client.Client client = ClientBuilder.newClient();
		WebTarget target = client.target(distanceMatrixAPI).queryParam("key", distanceMatrixAPITokenKey);
		JsonArray locations = new JsonArray();
		locations.add(origin);
		locations.add("Tunis , Tunisia");
		distanceMatrixParams.add("locations", locations);
		distanceMatrixParams.addProperty("allToAll", false);
		Response response = target.request().post(Entity.text(distanceMatrixParams));
		String responseStr = response.readEntity(String.class);
		client.close();
		return responseStr;
	}

	@Override
	public Client getClientWithTheHighestOrdersOccurency() {

		return (Client) manager
				.createQuery("SELECT c.client  from ClientOrder c group by max(c.client) " + "order by max(c.client) ")
				.getResultList().get(0);
	}

	@Override
	public Client getClientWithTheHighestOrdersSpendings() {
		return (Client) manager
				.createQuery("SELECT c.client from ClientOrder c   group by max(c.totale)" + "order by max(c.totale) ")
				.getResultList().get(0);
	}

// For admin : validate localOrders , need to implement statistics for orders  {group orders of them by clients , group orders by type,reduction ratio and status ,
//  most bought products.}

	public ClientOrder validateOrder(int id, int admin) {
		ClientOrder order = manager.find(ClientOrder.class, id);
		Agent agent = manager.find(Agent.class, admin);
		if ((order != null) && (order.getOrderNature() == OrderType.LocalPaymentMethod) && (!order.isValid())) {
			if (agent != null) {
				order.setValid(true);
				Invoice invoice = new Invoice();
				invoice.setOrderInvoice(order);
				Date date = new Date();
				invoice.setCreatedAt(new Timestamp(date.getTime()));
				manager.persist(invoice);
				manager.merge(order);
				if (order.getCart() != null) {
					order.getCart().setCheckedOut(true);
					manager.merge(order);
					manager.flush();
				}
				manager.flush();
				return order;
			} else
				return null;
		} else
			return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Product getBestProductOfAllTime() {
		List<ClientOrder> orders = manager.createQuery("SELECT C FROM ClientOrder C").getResultList();
		List<Product> products = manager.createQuery("SELECT P FROM Product P").getResultList();
		int max = 0;
		Product result = new Product();
		for (Product p : products) {
			int quantity = 0;
			for (ClientOrder order : orders) {
				for (CartProductRow row : order.getCart().getCartRows()) {
					if (row.getProduct().getId() == p.getId()) {
						quantity += row.getQuantity();
					}
				}
			}
			if (quantity > max) {
				max = quantity;
				result = p;
			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "unchecked" })
	@Override
	public Client getBestClientOfAllTime() {
		List<ClientOrder> orders = manager.createQuery("SELECT C FROM ClientOrder C").getResultList();
		List<Client> client = manager.createQuery("SELECT P FROM Client P").getResultList();
		double max = 0;
		Client result = new Client();
		for (Client p : client) {
			double quantity = 0;
			for (ClientOrder order : orders) {
				if (p.getId() == order.getCart().getClient().getId()) {
					quantity += order.getTotale();
				}
			}
			if (quantity > max) {
				max = quantity;
				result = p;
			}
		}
		return result;
	}

	@Override
	public long getWinLossPercentageBetweenTwoDays() {
		Date date = new Date();
		java.sql.Date todays = new java.sql.Date(date.getTime());
		java.sql.Date yesterday = new java.sql.Date(date.getTime());
		Date date3 = new Date();
		java.sql.Date d = new java.sql.Date(date3.getTime() - (60 * 60 * 24));
		date3.setTime(date3.getTime() - (60 * 60 * 60 * 24));
		return d.getTime();
	}

	@Override
	public List<ClientOrder> getOrdersByDate(String date) {
		try {
			@SuppressWarnings("unchecked")
			 Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(date);
			List<ClientOrder> orders = manager.createQuery("SELECT C FROM ClientOrder C WHERE C.createdAt=:date")
					.setParameter("date", new java.sql.Date(date1.getTime())).getResultList();
			return orders;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}

/*
 * try { ClientOrder yesterdaysOrder=(ClientOrder)manager.
 * createQuery("SELECT C FROM ClientOrder C WHERE C.createdAt=:date")
 * .setParameter("date",yesterday).getSingleResult(); ClientOrder
 * todaysOrder=(ClientOrder)manager.
 * createQuery("SELECT C FROM ClientOrder C WHERE C.createdAt=:date")
 * .setParameter("date", todays).getSingleResult(); if (yesterdaysOrder!=null &&
 * todaysOrder != null) {
 * 
 * return (todaysOrder.getTotale()-yesterdaysOrder.getTotale())/100; } else
 * return 0;} catch(NoResultException e) { return 0; }
 */