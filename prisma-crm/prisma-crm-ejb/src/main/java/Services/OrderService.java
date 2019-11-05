package Services;

import java.util.ArrayList;

import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
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

import Entities.Client;
import Entities.ClientOrder;
import Interfaces.IOrderLocal;

@Stateless
@LocalBean
public class OrderService implements IOrderLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	private final String distanceMatrixAPI = "http://www.mapquestapi.com/directions/v2/routematrix";
	private final String distanceMatrixAPITokenKey = "qgluQem4iTGKYyMxdp1MdsyGHnwwFdva";
	private final String payPalClientId = "AT2A3K3LpexZ8DK_LLEd0ozXd4i7AFh9kTEHWRF3puoW-gIjJFH_yt8rSax0fwphlkPNaIxeRV7bT_Bh";
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
		String crunchifyID = "";
		String crunchifySecret = "";
		String executionMode = "sandbox";
		RedirectUrls crunchifyRedirectUrls = new RedirectUrls();
		crunchifyRedirectUrls.setCancelUrl("http://localhost:3000/crunchifyCancel");
		crunchifyRedirectUrls.setReturnUrl("http://localhost:3000/crunchifyReturn");
		Payer crunchifyPayer = new Payer();
		crunchifyPayer.setPaymentMethod("paypal");
		Details crunchifyDetails = new Details();
		crunchifyDetails.setShipping("2.22");
		crunchifyDetails.setSubtotal("3.33");
		crunchifyDetails.setTax("1.11");
		Amount crunchifyAmount = new Amount();
		crunchifyAmount.setCurrency("USD");
		crunchifyAmount.setTotal("6.66");
		crunchifyAmount.setDetails(crunchifyDetails);
		Transaction crunchifyTransaction = new Transaction();
		crunchifyTransaction.setAmount(crunchifyAmount);
		crunchifyTransaction.setDescription("Crunchify Tutorial: How to Invoke PayPal REST API using Java Client?");
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
			crunchifyPaymentExecution.setPayerId("<!---- Add your PayerID here ---->");

			// This call will fail as user has to access Payment on UI. Programmatically
			// there is no way you can get Payer's consent.
			Payment createdAuthPayment = crunchifyPayment.execute(crunchifyapiContext, crunchifyPaymentExecution);

			Authorization crunchifyAuthorization = createdAuthPayment.getTransactions().get(0).getRelatedResources()
					.get(0).getAuthorization();

		} catch (PayPalRESTException e) {

			// The "standard" error output stream. This stream is already open and ready to
			// accept output data.
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

}
