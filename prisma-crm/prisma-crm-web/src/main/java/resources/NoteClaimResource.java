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
import Entities.NoteClaim;
import Entities.Claim;
import Enums.ClaimStatus;
import Interfaces.IClaimServiceRemote;
import Interfaces.INoteClaimRemote;
import Services.ClaimService;
import Services.NoteClaimService;
import Entities.Claim;
import javax.ejb.EJB;

@Path("notesClaim")
public class NoteClaimResource {

	@EJB
	public static NoteClaimService noteService = new NoteClaimService();
	@EJB
	public static ClaimService cs = new ClaimService();
	
	@GET
	@Path("claim/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNotesByClaim(@PathParam(value = "id") int id) {
		List<NoteClaim> notes = noteService.getNotesByClaimId(id);
		return Response.status(Status.OK).entity(notes).build();
	}
	
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		List<NoteClaim> notes = noteService.getAll();
		return Response.status(Status.OK).entity(notes).build();
	}
	
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNoteById(@PathParam(value = "id") int id) {
		NoteClaim note = noteService.getNoteById(id);
		return Response.status(Status.OK).entity(note).build();
	}

	@POST
	@Path("addNoteToClaim/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNotesToClaim(@PathParam(value = "id") int id, NoteClaim nc) {
		Claim c = cs.getById(id);
		NoteClaim note = noteService.addNoteClaim(nc,c);
		return Response.status(Status.OK).entity(note).build();
	}
	
	@DELETE
	@Path("/deleteNote/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNoteClaim(@PathParam(value = "id") int id) {
		return Response.status(Status.OK).entity(noteService.deletNoteClaimById(id)).build();
	}
	
	@PUT
	@Path("/editNoteClaim/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateNoteClaim(@PathParam(value = "id") int id, NoteClaim nc) {
		NoteClaim ncl = noteService.getNoteClaimByCode(id);
		NoteClaim injecter = nc;
		
		injecter.setId(ncl.getId());
		if (injecter.getDescription() == null) {
			injecter.setDescription(ncl.getDescription());
		}
		if (injecter.getClaim() == null) {
			injecter.setClaim(ncl.getClaim());
		}
		if (injecter.getCreatedBy() == null) {
			injecter.setCreatedBy(ncl.getCreatedBy());
		}
		injecter.setCreatedAt(ncl.getCreatedAt());
		NoteClaim newC = (NoteClaim) cs.merge(injecter);
		return Response.status(Status.OK).entity(newC).build();
	}
	
	
}
