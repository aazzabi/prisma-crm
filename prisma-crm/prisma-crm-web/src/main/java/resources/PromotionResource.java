package resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import Entities.Product;
import Entities.Promotion;
import Enums.Role;
import Interfaces.IPromotion;
import Services.UserService;
import utilities.RolesAllowed;

import java.util.List;

import javax.ejb.EJB;

@Path("/promotion")
public class PromotionResource {

	@EJB
	IPromotion promo;
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addPromotion(Promotion pr) {
		//if (UserService.UserLogged !=null) {	
		//	if(UserService.UserLogged.getRole() == Role.Admin) {
				promo.addPromotion(pr);
				return Response.ok(pr, MediaType.APPLICATION_JSON).build();
		//	} else {
			//	return "you are not allowed !";
		//	}
		//} return "Aucun user";
	}

	@POST
	@Path("/passerenpromotion/{idp}/{idpr}")
	@Produces(MediaType.APPLICATION_JSON)
    public Response passerenpromotion(@PathParam(value="idp")int idp,@PathParam(value="idpr")int idpr) {
	 promo.passerenpromotion(idp,idpr);
	return Response.status(Status.OK).build();
	}

	@DELETE
	@Path("/delete/{idp}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePromotion( @PathParam(value="idp")int idp) {
	 promo.deletePromotion(idp);
		return "deleted";
	}
	
	@GET
	@Path("/allpp")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allProducts() {
		return Response.status(Status.CREATED).entity(promo.listedesProduitpromotion()).build();

	}
	
	
	
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatePromotion(Promotion promotion) {

		
		return Response.status(Status.OK).entity(promo.updatePromotion(promotion)).build();

	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allPromotion() {
		return Response.status(Status.CREATED).entity(promo.listepromotions()).build();

	}

	

}
