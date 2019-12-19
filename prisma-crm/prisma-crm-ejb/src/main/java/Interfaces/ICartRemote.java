package Interfaces;

import java.util.List;

import javax.ejb.Remote;

import Entities.ClientCart;
import Entities.Product;
import Enums.OrderType;
import Utils.TimeDistance;

@Remote
public interface ICartRemote {
	public ClientCart createCart(ClientCart cart, int client);

	public Product addProductToCart(int product, int cart, int quantity, int points, boolean withReduction);

	public Product deleteProductFromCart(int product, int cart);

	// Creating new order
	public TimeDistance passToCheckOut(OrderType orderType, int client, int cart, double LONG, double LAT);

	// Send email to client when he forgot about his cart
	public boolean sendCartReminder(ClientCart cart);

	public List<ClientCart> fetchCarts();

	public List<ClientCart> getClientCarts(int clientid);

	public List<ClientCart> searchForClientCarts(String value, String criteria);

	public Product updateProductCartRow(int product, int cart, int quantity, int points, boolean withReduction);

}
