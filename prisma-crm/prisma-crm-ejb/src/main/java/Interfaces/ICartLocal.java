package Interfaces;

import javax.ejb.Local;

import Entities.ClientCart;
import Entities.Product;

@Local
public interface ICartLocal {

public ClientCart createCart(ClientCart cart,int client);	
public Product addProductToCart(int product,int cart,int quantity);
public Product deleteProductFromCart(int product,int cart);

}
