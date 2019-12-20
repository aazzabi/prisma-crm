package ws.ressources;

import java.sql.Timestamp;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Entities.Address;
import Entities.CartProductRow;
import Entities.ClientCart;
import Entities.ClientOrder;
import Entities.Product;
import Entities.ReductionFidelityRation;
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
	public Response createCart(@QueryParam(value = "id") int id) {
		ClientCart cart = new ClientCart();
		cart.setCheckedOut(false);
		Date date = new Date();
		cart.setCreatedAt(new Timestamp(date.getTime()));
		ClientCart result = cartService.createCart(cart, id);
		return result != null
				? Response.ok().status(Response.Status.CREATED).header("Access-Control-Allow-Origin", "*")
						.status(Response.Status.ACCEPTED).build()
				: Response.status(Response.Status.NOT_ACCEPTABLE).build();
	}

	// Adding a product for a given customer
	// works
	@Path("/add-product-to-cart")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProductToCart(@QueryParam("product") int product, @QueryParam("cart") int cart,
			@QueryParam("quantity") int quantity, @QueryParam("points") int points,
			@QueryParam("reduction") boolean reduction) {
		Product prdct = cartService.addProductToCart(product, cart, quantity, points, reduction);
		return prdct != null
				? Response.ok().status(Response.Status.CREATED).header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").entity(prdct).build()
				: Response.status(Response.Status.NOT_ACCEPTABLE).build();
	}

	// works
	@Path("/get-cart/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCurrentClientCarts(@PathParam(value = "id") int id) {
		List<ClientCart> carts = cartService.getClientCarts(id);
		return carts != null ? Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(carts).build()
				: Response.status(Response.Status.NOT_FOUND).build();

	}

	// works
	@Path("/delete-product-from-cart/{product}/{cart}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProductFromCart(@PathParam("product") int product, @PathParam("cart") int cart) {
		Product p = cartService.deleteProductFromCart(product, cart);
		return p != null
				? Response.ok().status(Response.Status.CREATED).entity(p).header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString()))
						.build();
	}

	//
	@Path("/pass-to-checkout")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response CheckOut(@FormParam("order") String ORDER_TYPE, @FormParam("client") int client,
			@FormParam("cart") int cart, @FormParam("longtitude") double longtitude,
			@FormParam("latitude") double latitude) {
		OrderType orderT;
		switch (ORDER_TYPE) {
		case "localPayment":
			orderT = OrderType.LocalPaymentMethod;
			break;
		case "onlinePayment":
			orderT = OrderType.OnlinePaymentMethod;
			break;
		default:
			return Response.status(Response.Status.NOT_ACCEPTABLE)
					.entity(new String(Response.Status.NOT_ACCEPTABLE.toString())).build();
		}
		TimeDistance result = cartService.passToCheckOut(orderT, client, cart, longtitude, latitude);
		return result != null
				? Response.ok().status(Response.Status.CREATED).entity(result)
						.header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString()))
						.build();
	}

	@Path("/getNearesAddress/{lon}/{lat}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response testAddressDistance(@PathParam(value = "lon") double lon, @PathParam(value = "lat") double lat) {

		return Response.ok().status(Response.Status.CREATED)
				.entity(cartService.getNearestStoreAddress(lon, lat))
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").build();

	}

	@Path("/testTime")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response testTimeDistance(@FormParam("lon") double lon, @FormParam("lat") double lat) {
		TimeDistance result = cartService.getTimeNeededToGetOrderProducts(lon, lat, 19);
		return Response.ok().entity(result).build();
	}

	@Path("/stores-cart-relation/{origin}/{destination}/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartStores(@PathParam(value = "origin") double origin,
			@PathParam(value = "destination") double destination, @PathParam(value = "id") int id) {
		TimeDistance dst = cartService.getTimeNeededToGetOrderProducts(origin, destination, id);
		return Response.ok().entity(dst).header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").build();
	}

	@Path("/update-cart-row/{cart}/{product}/{quantity}/{points}/{reduction}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProductCartRowService(@PathParam("cart") int cart, @PathParam("product") int product,
			@PathParam("quantity") int quantity, @PathParam("points") int points,
			@PathParam("reduction") boolean reduction) {
		Product result = cartService.updateProductCartRow(product, cart, quantity, points, reduction);
		return result != null
				? Response.ok().status(Response.Status.CREATED).header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").entity(result).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString()))
						.build();
	}

	@Path("/get-cart-rows/{id}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartRows(@PathParam(value = "id") int id) {
		Set<CartProductRow> result = cartService.getCart(id).getCartRows();
		return result != null
				? Response.ok().status(Response.Status.CREATED).header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").entity(result).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString()))
						.build();
	}

	// works
	@Path("/get-products")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProductsWS() {
		List<Product> carts = cartService.fetchProductsFromCart();
		return carts != null ? Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(carts).build()
				: Response.status(Response.Status.NOT_FOUND).build();

	}

	@Path("/get-product/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProductById(@PathParam(value = "id") int id) {
		Product product = cartService.getProductById(id);
		return product != null ? Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(product).build()
				: Response.status(Response.Status.NOT_FOUND).build();

	}

	@Path("/get-ReductionRatio/{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProductReductionRatio(@PathParam(value = "id") int id) {
		ReductionFidelityRation r = cartService.getProductReductionRatio(id);
		return r != null
				? Response.ok().header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(r).build()
				: Response.status(Response.Status.NOT_FOUND).build();
	}

	// getProductCartRow
	@Path("/get-product-row/{product}/{cart}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProductRow(@PathParam(value = "product") int product, @PathParam(value = "cart") int cart) {
		CartProductRow r = cartService.getProductCartRow(product, cart);
		return r != null
				? Response.ok().header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(r).build()
				: Response.status(Response.Status.NO_CONTENT).entity(null).build();
	}

	// resetUserPoints
	@Path("/reset-product-client-data/{product}/{quantity}/{client}/{fidelity}/{cart}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response resetProductCart(@PathParam(value = "product") int product,
			@PathParam(value = "quantity") int quantity, @PathParam(value = "client") int client,
			@PathParam(value = "fidelity") int fidelity, @PathParam(value = "cart") int cart) {
		CartProductRow r = cartService.resetUserPoints(client, fidelity, quantity, product, cart);
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(r).build();

	}
	
	//passToCheckOutUsingCash
	//int cartId, int clientId, double distanceCS,int storeId
	@Path("/checkOutOrderCash/{cartId}/{clientId}/{distanceCS}/{storeId}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkOutCashPayment(
			@PathParam(value = "cartId") int cartId,
			@PathParam(value = "clientId") int clientId, @PathParam(value = "distanceCS") double distanceCS,
			@PathParam(value = "storeId") int storeId) {
			float r=cartService.passToCheckOutUsingCash(cartId, clientId, distanceCS, storeId);
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(r).build();

	}
	
	@Path("/delete-cart/{cartId}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteCart(
			@PathParam(value = "cartId") int cartId) {
			cartService.deleteCart(cartId);
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity("cart deleted").build();
	}
	
	//passToCheckOutUsingPayPal
	//passToCheckOutUsingCash
	//int cartId, int clientId, double distanceCS,int storeId
	@Path("/checkOutOrderOnline/{cartId}/{clientId}/{distanceCS}/{storeId}")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response checkOutOnlinePayment(
			@PathParam(value = "cartId") int cartId,
			@PathParam(value = "clientId") int clientId, @PathParam(value = "distanceCS") double distanceCS,
			@PathParam(value = "storeId") int storeId) {
			float r=cartService.passToCheckOutUsingPayPal(cartId, clientId, distanceCS, storeId);
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(r).build();

	}
	

	@Path("/get-client-orders/{client}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkOutOnlinePayment(@PathParam(value="client") int  client) {
				List<ClientOrder> orders=cartService.getClientOrders(client);
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(orders).build();
	}
	
	@Path("/get-cart-by-id/{cart}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCartById(@PathParam(value="cart") int  cart) {
		ClientCart result =cartService.getCartById(cart);				
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(result).build();
	}
	
	@Path("/get-order-rows/{order}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrderRows(@PathParam(value="order") int  order) {
		Set<CartProductRow> result =cartService.getOrderRows(order);				
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(result).build();
	}
	
	@Path("/get-specific-order/{order}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSpecificOrder(@PathParam(value="order") int  order) {
		ClientOrder result =cartService.getSpecificClientOrder(order);				
		return Response.ok().header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
				.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
				.header("Access-Control-Max-Age", "1209600").status(Response.Status.ACCEPTED).entity(result).build();
	}
	
	
}
