package resources.reclamations;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import Entities.Agent;
import Entities.Client;
import Entities.NoteClaim;
import Entities.Claim;
import Enums.ClaimStatus;
import Enums.Role;
import Interfaces.IClaimServiceRemote;
import Interfaces.INoteClaimRemote;
import Services.ClaimService;
import Services.NoteClaimService;
import Services.UserService;
import utilities.RolesAllowed;
import Entities.Claim;
import javax.ejb.EJB;

@Path("reclamation")
public class FrontClaimsResource {

	@EJB
	public static NoteClaimService noteService = new NoteClaimService();
	@EJB
	public static ClaimService cs = new ClaimService();
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addClaim(Claim c) {
		c.setId(cs.addClaim(c));
		return Response.status(Status.CREATED).entity(c).build();
	}
	
	@GET
	@Path("/code/{c}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaimByID(@PathParam(value = "c") String c) {
		Claim claim = cs.getByCode(c);
		if (claim == null) {
			return Response.status(Status.NOT_FOUND).build();
		} else if (claim.getCreatedBy()== UserService.UserLogged) {
			return Response.status(Status.CREATED).entity(claim).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("you are not allowed").build();
		}
	}	
	
	
	@GET
	@RolesAllowed(Permissions = {Role.technical})
	@Path("FAQ")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllFaq() {
		return Response.status(Status.CREATED).entity(cs.getAllFaq()).build();
	}
	
	@GET
	@Path("FAQ/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFaqById(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(cs.getFaqById(id)).build();
	}
	
	
	
}
