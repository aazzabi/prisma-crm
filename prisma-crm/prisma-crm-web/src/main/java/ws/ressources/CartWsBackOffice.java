package ws.ressources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import Entities.ClientCart;
import Services.CartService;

@Path("/cartBack")
@javax.enterprise.context.RequestScoped
public class CartWsBackOffice {
	// stats goes there
	@Inject
	CartService cartService;
	
	@GET
	@Path("/getCarts")
	public String fetchCarts() {
		String result="";
		for (ClientCart c :cartService.fetchCarts())
		{
		result+="id : "+c.getId()	;
		}
		return result;
	}
}
