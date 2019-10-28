package Interfaces;

import java.util.List;

import javax.ejb.Local;

import Entities.Client;
import Entities.ClientCart;
import Entities.ClientOrder;

@Local
public interface IOrderLocal {

	public String addOrder(int clientId, ClientOrder order,ClientCart cart);

	public List<ClientOrder> fetchOrders();

	public ClientOrder getOrder(int id);

	public ClientOrder updateClientOrder(ClientOrder order);

	public boolean deleteClientOrder(int id);

	public List<ClientOrder> searchForOrders(String criteria, String value);

	public boolean convertClientFidelityPointsToReduction(ClientOrder order);

	public void checkOutOrder(ClientOrder order);

	public List<ClientOrder> fetchAllClientOrders(Client client);

}
