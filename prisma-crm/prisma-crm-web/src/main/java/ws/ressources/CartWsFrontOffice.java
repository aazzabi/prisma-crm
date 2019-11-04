package ws.ressources;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Entities.Address;
import Entities.ClientCart;
import Entities.ClientOrder;
import Entities.Product;
import Entities.Store;
import Enums.OrderType;
import Services.CartService;
import Utils.TimeDistance;

@Path("/cart")
@javax.enterprise.context.RequestScoped
public class CartWsFrontOffice {
	@Inject
	CartService cartService;

	// Creating new cart for a given customer
	// Works 
	@Path("/new")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createCart(@FormParam("id") int id) {
		ClientCart cart = new ClientCart();
		cart.setCheckedOut(false);
		Date date = new Date();
		cart.setCreatedAt(new Timestamp(date.getTime()));
		ClientCart result = cartService.createCart(cart, id);
		return result != null ? Response.ok().status(Response.Status.CREATED).entity(result).build()
				: Response.status(Response.Status.NOT_ACCEPTABLE).build();
	}

	// Adding a product for a given customer
	// works
	@Path("/add-product-to-cart")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addProductToCart(@FormParam("product") int product, @FormParam("cart") int cart,
			@FormParam("quantity") int quantity, @FormParam("points") int points,
			@FormParam("reduction") boolean reduction) {
		Product prdct = cartService.addProductToCart(product, cart, quantity, points, reduction);
		return prdct != null ? Response.ok().status(Response.Status.CREATED).entity(prdct).build()
				: Response.status(Response.Status.NOT_ACCEPTABLE).build();
	}

	//works
	@Path("/get-cart/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentClientCarts(@PathParam(value = "id") int id) {
		List<ClientCart> carts = cartService.getClientCarts(id);
		return carts != null ? Response.ok().status(Response.Status.ACCEPTED).entity(carts).build()
				: Response.status(Response.Status.NOT_FOUND).build();

	}

	//works
	@Path("/delete-product-from-cart")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response deleteProductFromCart(@FormParam("product") int product,@FormParam("cart") int cart) {
		Product p = cartService.deleteProductFromCart(product, cart);
		return p != null ? Response.ok().status(Response.Status.CREATED).entity(p).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString())).build();
	}
	
	
	//
	@Path("/pass-to-checkout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response CheckOut(@FormParam("order") String ORDER_TYPE ,@FormParam("client") int client ,
			@FormParam("cart") int cart,@FormParam("longtitude") double longtitude,@FormParam("latitude") double latitude )
	{
		OrderType orderT;
		switch (ORDER_TYPE)
		{
		case "localPayment" : 
			orderT=OrderType.LocalPaymentMethod;
			break;
		case "onlinePayment":
			orderT=OrderType.OnlinePaymentMethod;
			break;
			default:
				return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new String(Response.Status.NOT_ACCEPTABLE.toString())).build();				
		}
		TimeDistance result=cartService.passToCheckOut(orderT, client, cart,longtitude,latitude);
		return result!=null?Response.ok().status(Response.Status.CREATED).entity(result).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString())).build();
	}
	
	@Path("/getNearesAddress/{lon}/{lat}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String testAddressDistance(@PathParam(value="lon") double lon,@PathParam(value="lat") double lat)
	{
		
		return cartService.getNearestStoreAddress(lon,lat).getName();
		//return cartService.reverseGeoCode(lon, lat);
		
	}
	
	@Path("/testTime")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response testTimeDistance(@FormParam("lon") double lon,@FormParam("lat") double lat)
	{
		TimeDistance result=cartService.getTimeNeededToGetOrderProducts(lon, lat,19);
		return Response.ok().entity(result).build();
	}
	
	@Path("/stores-cart-relation/{origin}/{destination}/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartStores(@PathParam(value="origin") double  origin,@PathParam(value="destination") 
	double  destination,@PathParam(value="id") int  id)
	{
		TimeDistance dst=cartService.getTimeNeededToGetOrderProducts(origin,destination,id);
		return Response.ok().entity(dst).build();
	}

}
