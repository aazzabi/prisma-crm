package ws.ressources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Services.InvoiceService;
import Services.OrderService;

@Path("/orders")
@javax.enterprise.context.RequestScoped
public class OrdersWsFrontOffice {	
	@Inject
	OrderService businessOrder;
	@Inject
	InvoiceService businessInvoice;
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response testKheffa()
	{
		return Response.ok().entity(businessOrder.calculateDistanceBetweenClientAndStore(null,"Bardo, Tunisia")).build();
	}

	
	@GET
	@Path("{value}/{base}/{target}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response testingCurrency(@PathParam(value="base") String base,@PathParam(value="target") String target,
			@PathParam(value="value") String value)
	{
		return Response.ok().entity(businessInvoice.getCurrencyCurrentValue(base, target,Float.parseFloat(value))).build();
	}
}
