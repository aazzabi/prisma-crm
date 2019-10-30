package Services;


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

	@Override
	public ClientOrder updateClientOrder(ClientOrder order) {
		if (getOrder(order.getId()) != null) {
			manager.merge(order);
			return order;
		} else
			return null;
	}

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

	@Override
	public void checkOutOrder(ClientOrder order) {

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
		locations.add(order.getStore().getAddress().getDisplayName());
		distanceMatrixParams.add("locations", locations);
		distanceMatrixParams.addProperty("allToAll", false);
		Response response = target.request().post(Entity.text(distanceMatrixParams));
		String responseStr = response.readEntity(String.class);
		client.close();
		return responseStr;
	}

	@Override
	public Client getClientWithTheHighestOrdersOccurency() {
		
		return (Client) manager.createQuery("SELECT c.client  from ClientOrder c group by max(c.client) "
										  + "order by max(c.client) ").getResultList().get(0);
	}

	@Override
	public Client getClientWithTheHighestOrdersSpendings() {
		return (Client) manager.createQuery("SELECT c.client from ClientOrder c   group by max(c.totale)" + 
											"order by max(c.totale) ").getResultList().get(0);

	}

}
