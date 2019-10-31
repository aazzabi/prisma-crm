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

@Path("dashboard/reclamation")
public class DashClaimResource {

	@EJB
	public static NoteClaimService noteService = new NoteClaimService();
	@EJB
	public static ClaimService cs = new ClaimService();
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllClaim() {	
		if (UserService.UserLogged.getRole()== Role.Client) {
			return Response.status(Status.CREATED).entity(cs.getAll()).build();
		} else if ( (UserService.UserLogged.getRole()== Role.financial) 
				|| (UserService.UserLogged.getRole()== Role.technical) 
				|| (UserService.UserLogged.getRole()== Role.relational) ) {
			return Response.status(Status.CREATED).entity(cs.getClaimsByResponsable((Agent)UserService.UserLogged)).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaim(@PathParam(value = "id") int id) {	
		Claim c = cs.getById(id);
		if ((c.getResponsable()!= UserService.UserLogged) || (c.getCreatedBy()!= UserService.UserLogged)  || (c.getFirstResponsable()!= UserService.UserLogged) ) {
			return Response.status(Status.NOT_FOUND).entity("You are not allowed").build();
		} else {
			return Response.status(Status.CREATED).entity(c).build();	
		}
	}
	
	
	

}
