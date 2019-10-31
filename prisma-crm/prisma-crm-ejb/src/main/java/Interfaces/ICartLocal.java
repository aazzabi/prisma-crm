package Interfaces;

import java.util.List;

import javax.ejb.Local;

import Entities.CartProductRow;
import Entities.ClientCart;
import Entities.ClientOrder;
import Entities.Product;
import Enums.OrderType;

@Local
public interface ICartLocal {


	
public ClientCart createCart(ClientCart cart,int client);	

public Product addProductToCart(int product,int cart,int quantity,int points,boolean withReduction);

public Product deleteProductFromCart(int product,int cart);

//Creating new order
public ClientOrder passToCheckOut(OrderType orderType,int client,int cart);

//Send email to client when he forgot about his cart
public boolean sendCartReminder(ClientCart cart);

public List<ClientCart> fetchCarts();

public List<ClientCart> getClientCarts(int clientid);

public List<ClientCart> searchForClientCarts(String value,String criteria);


}
