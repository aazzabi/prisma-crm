package ws.ressources;

import java.sql.Date;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Entities.Client;
import Entities.ClientOrder;
import Entities.Product;
import Services.OrderService;

@Path("/orderAd")
@javax.enterprise.context.RequestScoped
public class OrdersWsBackOffice {

	@Inject
	OrderService orderBusiness;

	@Path("/validateOrder/{order}/{admin}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)

	public Response validateOrder(@PathParam(value = "order") int order, @PathParam(value = "admin") int admin) {
		ClientOrder OO = orderBusiness.validateOrder(order, admin);

		return OO != null ? Response.ok().status(Response.Status.CREATED).entity(OO).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString()))
						.build();
	}

	// fetchOrders
	@Path("/back/fetch-orders")
	@POST
	@Produces(MediaType.APPLICATION_JSON)

	public Response fetchOrders() {
		List<ClientOrder> OO = orderBusiness.fetchOrders();
		return OO != null
				? Response.ok().status(Response.Status.CREATED).header("Access-Control-Allow-Origin", "*")
						.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
						.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
						.header("Access-Control-Max-Age", "1209600").entity(OO).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString()))
						.build();
	}

	// Getting best product of the week
	@Path("/back/product-of-allTime")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBestProductOfTheAllTime()
	{
		Product p =orderBusiness.getBestProductOfAllTime();
		return		Response.ok().status(Response.Status.CREATED)
		.header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.header("Access-Control-Max-Age", "1209600").entity(p).build();
	}
	
	@Path("/back/client-of-all-time")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBestClientOfTheAllTime()
	{
		Client p =orderBusiness.getBestClientOfAllTime();
		return		Response.ok().status(Response.Status.CREATED)
		.header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.header("Access-Control-Max-Age", "1209600").entity(p).build();
	}
	

	@Path("/back/win-loss-average")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWinLossAverageFromYesterday()
	{
		double p=orderBusiness.getWinLossPercentageBetweenTwoDays();
		java.util.Date x=new java.util.Date();
		Date date=new Date(x.getTime()-(60*60*60*24));
		return		Response.ok().status(Response.Status.CREATED)
		.header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.header("Access-Control-Max-Age", "1209600").entity(date).build();
	}
	
	@Path("/back/get-orders-date/{date}")
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response getOrdersByDate(@PathParam(value="date")String date)
	{
		List<ClientOrder> orders=orderBusiness.getOrdersByDate(date);
		return		Response.ok().status(Response.Status.CREATED)
		.header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.header("Access-Control-Max-Age", "1209600").entity(orders).build();
	}
	
	

	
	
}
