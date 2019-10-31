package resources;

import javax.ejb.EJB;
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

import Entities.Offer;
import Interfaces.IOffer;

@Path("/offer")
public class OfferResource {
	@EJB
      IOffer	off ;
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addOffer(Offer o) {
	Offer of= off.addOffer(o);
		return Response.status(Status.CREATED).entity(of).build();

	}
	
	@DELETE
	@Path("/delete/{ido}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteoffer( @PathParam(value="ido")int ido) {
	 off.deleteOffer(ido);
		return "deleted";
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateoffer(Offer offer) {

	
	return Response.status(Status.OK).entity(off.updateOffer(offer)).build();

	}
	
	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response alloffers() {
		return Response.status(Status.CREATED).entity(off.listeoffer()).build();

	}

}
