package ws.ressources;

import java.sql.Timestamp;

import javax.ejb.EJB;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Entities.Client;
import Entities.ClientCart;
import Entities.ClientOrder;
import Enums.ClientGroups;
import Enums.ClientType;
import Services.CartService;
import Services.OrderService;

@Path("/orders")
@javax.enterprise.context.RequestScoped
public class WSOrderRessourcesBackOffice {	
	@Inject
	OrderService orderService;
	@Inject
	CartService cartService;
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String createClientOrder()
	{
		ClientOrder order=new ClientOrder();
		order.setOrderNature("kadhe");
		order.setReductionRatio(0);		
		order.setTotale(100);
		order.setValid(true);
		return 		orderService.addOrder(1, order,null);
	}
	
	@Path("/getOrders")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllClientOrders()
	{
		return orderService.fetchOrders().size()>0
				?Response.ok().entity(orderService.fetchOrders()).build()
						:Response.status(Response.Status.NO_CONTENT).type("No Orders found").build();
	}
	
	@Path("/createCart/{client}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response createCart(@PathParam(value="client") String client)
	{
		ClientCart cart=new ClientCart();
		cart.setCheckedOut(false);
		cart.setCreatedAt(new Timestamp(874452));
		cart.setUpdatedAt(new Timestamp(874459));
		cartService.createCart(cart, Integer.parseInt(client));
		return Response.ok().build();
	}
	
	@Path("/addProductToCart/{product}/{cart}/{quantity}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response addProductToResponse(@PathParam(value="product") String product,@PathParam(value="cart") String cart,
			@PathParam(value="quantity") String quantity)
	{
		cartService.addProductToCart(Integer.parseInt(product), Integer.parseInt(cart), Integer.parseInt(quantity));
		return Response.ok().build();
	}
	
	@Path("/removeProductToCart/{product}/{cart}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response removeProductToResponse(@PathParam(value="product") String product,@PathParam(value="cart") String cart)
	{
		cartService.deleteProductFromCart(Integer.parseInt(product), Integer.parseInt(cart));
		return Response.ok().build();
	}
	
	

	
	

	
	
	
}
