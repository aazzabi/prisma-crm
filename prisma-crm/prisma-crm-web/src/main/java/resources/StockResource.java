package resources;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import Entities.Stock;
import Entities.Store;
import Interfaces.IStockServiceLocal;
import Interfaces.IStoreServiceLocal;
import Services.StockService;



@Path("/stock")
public class StockResource {
	@EJB
	IStockServiceLocal service;
	@EJB
	IStoreServiceLocal storeServ;

	
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addStock(@QueryParam(value="idStore")int idStore,Stock stock) {
		Store store = storeServ.findStoreById(idStore);
		stock.setStore(store);

		return Response.status(Status.CREATED).entity(service.addStock(stock,store)).build();
		
	}

}
