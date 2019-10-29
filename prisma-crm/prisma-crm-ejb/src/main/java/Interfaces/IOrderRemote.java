package Interfaces;

import javax.ejb.Remote;

import Entities.Client;
import Entities.ClientOrder;
@Remote
public interface IOrderRemote {
	public String addOrder(Client client,ClientOrder order);	

}
