package Services;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import Entities.Client;
import Entities.ClientCart;
import Entities.ClientOrder;
import Interfaces.IOrderLocal;

@Stateless
@LocalBean
public class OrderService implements IOrderLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;

	@Override
	public String addOrder(int clientId, ClientOrder order,ClientCart cart) {
		Client client = manager.find(Client.class, clientId);
		if (client != null) {
			order.setClient(client);
			manager.persist(order);
			manager.flush();
			return "Creating Order for Mr/Ms " + client.getFullName();
		} else
			return "Client not found";
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
	public boolean convertClientFidelityPointsToReduction(ClientOrder order) {
		if(order.getClient().getFidelityScore()>0)
		{
			return true;
		}
		else
		return false;
	}

	@Override
	public void checkOutOrder(ClientOrder order) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ClientOrder> fetchAllClientOrders(Client client) {
		if (manager.find(Client.class, client.getId())!=null)
		{
			return manager.createQuery("SELECT c FROM ClientOrder where c.client =:client").setParameter("client", client).getResultList();
		}
		else
		return null;
	}

}
