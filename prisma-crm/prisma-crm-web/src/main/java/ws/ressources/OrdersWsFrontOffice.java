package ws.ressources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import Entities.Invoice;
import Services.InvoiceService;
import Services.OrderService;

@Path("/orders")
@javax.enterprise.context.RequestScoped
public class OrdersWsFrontOffice {	
	@Inject
	OrderService businessOrder;
	@Inject
	InvoiceService businessInvoice;
	
	
	//Test service de calcule distance entre deux adresses
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response testKheffa()
	{
		return Response.ok().entity(businessOrder.calculateDistanceBetweenClientAndStore(null,"Bardo, Tunisia")).build();
	}

	
	

	@Path("/create-order-invoice")
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateOrderInvoice(@FormParam("order")int id)
	{
		Invoice result=businessInvoice.createInvoice(id);
		return result!=null?Response.ok().status(Response.Status.CREATED).entity(result).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString())).build();
		
	}
	
	@Path("/get-invoice-product-rows/{id}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response fetchInvoiceProductRows(@PathParam(value="id") int invoiceId)
	{
		String result=businessInvoice.getInvoiceProductRows(invoiceId);
		return result!=null?Response.ok().status(Response.Status.CREATED).entity(result).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString())).build();
	}
	
	@Path("/convert-Order-Currency/{invoice}/{CURRENCY}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response convertInvoiceCurrency(@PathParam(value="CURRENCY") String currency,@PathParam(value="invoice")int invoiceId)
	{
		String result=businessInvoice.getCurrencyCurrentValue("TND",currency,invoiceId);
		return result!=null?Response.ok().status(Response.Status.CREATED).entity(result).build()
				: Response.status(Response.Status.NOT_FOUND).entity(new String(Response.Status.NOT_FOUND.toString())).build();
	}
	
	
	
}
