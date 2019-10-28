package Services;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import Entities.CartProductRow;
import Entities.Client;
import Entities.ClientCart;
import Entities.Product;
import Interfaces.ICartLocal;

@Stateless
@LocalBean
public class CartService implements ICartLocal {
	@PersistenceContext(unitName = "prisma-crm-ejb")
	EntityManager manager;
	@Override
	public ClientCart createCart(ClientCart cart,int client) {
		Client cl=manager.find(Client.class, client);
		if (cl!=null)
		{
		cart.setClient(cl);	
		manager.persist(cart);
		manager.flush();
		return cart;
		}
		else 
		return null;
	}

	@Override
	public Product addProductToCart(int product, int cart, int quantity) {
		if((manager.find(Product.class, product)!=null)
				&& (manager.find(ClientCart.class, cart)!=null))
		{
			CartProductRow row=new CartProductRow();
			row.setCart(manager.find(ClientCart.class, cart));
			row.setProduct(manager.find(Product.class, product));
			manager.persist(row);
			manager.flush();
			return null;
		}
		else
		return null;
	}

	@Override
	public Product deleteProductFromCart(int product, int cart) {
		if((manager.find(Product.class, product)!=null)
				&& (manager.find(ClientCart.class, cart)!=null))
		{
		ClientCart crt=manager.find(ClientCart.class,cart);
		Product prdct=manager.find(Product.class,product);
		CartProductRow row=(CartProductRow)manager.createQuery("SELECT c FROM CartProductRow c "
											 + "where c.cart =:cart AND c.product=:product")
								                  .setParameter("cart",crt)
								                  .setParameter("product",prdct)
								                  .getSingleResult();
		manager.remove(row);
		manager.flush();
		}
		return null;
	}
	
	

}
