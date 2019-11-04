package resources;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import Entities.Product;
import Entities.Stock;
import Entities.Store;
import Enums.ProductType;
import Interfaces.IProductServiceLocal;
import Interfaces.IStockServiceLocal;
import Interfaces.IStoreServiceLocal;
import Services.StockService;



@Path("/stock")
public class StockResource {
	@EJB
	IStockServiceLocal service;
	@EJB
	IStoreServiceLocal storeServ;
	@EJB
	IProductServiceLocal prodServ;



	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addStock(@QueryParam(value="idStore")int idStore,Stock stock) {
		Store store = storeServ.findStoreById(idStore);
		stock.setStore(store);

		return Response.status(Status.CREATED).entity(service.addStock(stock,store)).build();

	}

	@GET
	@Path("/add_products")
	@Produces(MediaType.APPLICATION_JSON)
	public String addProducts(@QueryParam(value="idStore")int idStore,@QueryParam(value="ref")String ref ,@QueryParam(value="quantity")int q) {
		Store store = storeServ.findStoreById(idStore);
		Product p = prodServ.findProductsByReference(ref).get(0);
		p.setId(0);
		p.setStore(store);
		//Product product = new Product(p.getReference(),p.getName(),p.getDescription(),p.getType(),p.getGuarantee(),p.getPrice());
		
		/*for(int i=1;i<=q;i++) {
			
			System.out.println("quantity=="+q+" i======"+i);
			System.out.println(prodServ.addProduct(p));
			store.getProducts().add(p);
			
		}*/
	
		return "products added";

	}
	
	@GET
	@Path("/check_stock")
	@Produces(MediaType.APPLICATION_JSON)
	public  Stock checkStock(@QueryParam(value="idStore")int idStore,@QueryParam(value="ref")String ref) {
		
		Stock stock = service.checkStock(idStore, ref);
		
		stock.setQuantity(stock.getQuantity()-1);
		
		//if(stock.getQuantityMin())
		return stock;
		
	}

}