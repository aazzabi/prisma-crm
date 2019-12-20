package Interfaces;

import java.util.List;

import javax.ejb.Remote;

import Entities.Client;
import Entities.ClientOrder;
@Remote
public interface IOrderRemote {
	public List<ClientOrder> fetchOrders();

	public ClientOrder getOrder(int id);

	public ClientOrder updateClientOrder(ClientOrder order);

	public boolean deleteClientOrder(int id);

	public List<ClientOrder> searchForOrders(String criteria, String value);

	public void checkOutOrder(ClientOrder order);

	public List<ClientOrder> fetchAllClientOrders(Client client);
	
	public String calculateDistanceBetweenClientAndStore(ClientOrder order,String origin);
	
	public Client getClientWithTheHighestOrdersOccurency();
	
	public Client getClientWithTheHighestOrdersSpendings();

}
