package resources;

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
import Entities.Claim;
import Enums.ClaimStatus;
import Interfaces.IClaimServiceLocal;
import Services.ClaimService;
import Entities.Claim;
import javax.ejb.EJB;

@Path("claim")
public class ClaimResource {

	@EJB
	public static ClaimService cs = new ClaimService();
	/*
	 * @POST
	 * 
	 * @Path("new")
	 * 
	 * @Consumes(MediaType.APPLICATION_JSON)
	 * 
	 * @Produces(MediaType.APPLICATION_JSON) public Response addClaim(Claim c) {
	 * return Response.status(Status.CREATED).entity(cs.addClaim(c)).build(); //if
	 * (cs.addClaim(c) != 0) { // return
	 * Response.status(Status.CREATED).entity(cs.addClaim(c)).build(); //} else { //
	 * return Response.status(Status.BAD_REQUEST).build(); //} //return
	 * Response.status(Status.CREATED).entity(CSL.clienttoJson(c)).build(); }
	 */

	@POST
	@Path("new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addClaim(Claim c) {
		c.setId(cs.addClaim(c));
		return Response.status(Status.CREATED).entity(cs.addClaim(c)).build();
	}

	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllClaim() {
		return Response.status(Status.CREATED).entity(cs.getAll()).build();
	}

	@GET
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaimByID(@PathParam(value = "id") int id) {
		Claim c = cs.getById(id);
		// return Response.status(Status.CREATED).entity(cs.claimToJSON(c)).build();
		return Response.status(Status.CREATED).entity(cs.getById(id)).build();
	}

	@GET
	@Path("/code/{c}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaimByID(@PathParam(value = "c") String c) {
		Claim claim = cs.getByCode(c);
		if (claim == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.status(Status.CREATED).entity(claim).build();
	}	
	
	@PUT
	@Path("/editClaim/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateClaim(@PathParam(value = "id") int id, Claim c) {
		Claim cl = cs.getById(id);		
		Claim injecter = c;
		injecter.setId(cl.getId());		
		if (injecter.getStatus()==null) {injecter.setStatus(cl.getStatus());}
		if (injecter.getPriority()==null) {injecter.setPriority(cl.getPriority());}
		if (injecter.getType()==null) {injecter.setType(cl.getType());}
		if (injecter.getResponsable()==null) {injecter.setResponsable(cl.getResponsable());}
		if (injecter.getResolvedAt()==null) {injecter.setResolvedAt(cl.getResolvedAt());}
		if (injecter.getCode()==null) {injecter.setCode(cl.getCode());}
		if (injecter.getTitle()==null) {injecter.setTitle(cl.getTitle());}
		if (injecter.getOpenedAt()==null) {injecter.setOpenedAt(cl.getOpenedAt());}
		if (injecter.getCreatedBy()==null) {injecter.setCreatedBy(cl.getCreatedBy());}
		if (injecter.getCreatedAt()==null) {injecter.setCreatedAt(cl.getCreatedAt());}
		if (injecter.getResolvedBy()==null) {injecter.setResolvedBy(cl.getResolvedBy());}
		if (injecter.getDescription()==null) {injecter.setDescription(cl.getDescription());}
		
		Claim newC = (Claim) cs.merge(injecter);
		return Response.status(Status.OK).entity(newC).build();
	}
	
	@PUT
	@Path("/archiverCalim/{id}")
	public Response archiverCalim(@PathParam(value = "id") int id){
		Claim newC = cs.getById(id);
		cs.changeStatus(newC, ClaimStatus.FERME_SANS_SOLUTION);
		return Response.status(Status.OK).build();
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteClaim(@PathParam(value = "id") int id) {
		return Response.status(Status.OK).entity(cs.deleteClaimById(id)).build();
	}
	
	

}
