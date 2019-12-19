package Interfaces;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import Entities.CartProductRow;
import Entities.ClientCart;
import Entities.ClientOrder;
import Entities.Product;
import Entities.ReductionFidelityRation;
import Entities.Store;
import Enums.OrderType;
import Utils.TimeDistance;

@Local
public interface ICartLocal {

	public ClientCart createCart(ClientCart cart, int client);

	public Product addProductToCart(int product, int cart, int quantity, int points, boolean withReduction);

	public Product deleteProductFromCart(int product, int cart);

//Creating new order
	public TimeDistance passToCheckOut(OrderType orderType, int client, int cart, double LONG, double LAT);
	
	public float passToCheckOutUsingCash(int cartId,int clientId,double distanceCS,int storeId);
	
	public float passToCheckOutUsingPayPal(int cartId,int clientId,double distanceCS,int storeId);

//Send email to client when he forgot about his cart
	public boolean sendCartReminder(ClientCart cart);

	public List<ClientCart> fetchCarts();

	public List<ClientCart> getClientCarts(int clientid);

	public List<ClientCart> searchForClientCarts(String value, String criteria);

	public Product updateProductCartRow(int product, int cart, int quantity, int points, boolean withReduction);

	public List<Product> fetchProductsFromCart();
	
	public Product getProductById(int id);
	
	public ReductionFidelityRation getProductReductionRatio(int productId);
	
	public CartProductRow getProductCartRow(int productId,int cartId);
	
	public CartProductRow resetUserPoints(int clientId,int points,int productQuantity,int productId,int cartId);
	
	public Set<CartProductRow> getCartRows(int cartId);
	
	public void deleteCart(int cart);
	
	public List<ClientOrder> getClientOrders(int id);
	
	public ClientCart getCartById(int id);
	
	public Set<CartProductRow> getOrderRows(int id);
	
	public ClientOrder getSpecificClientOrder(int id);
	
	

}
