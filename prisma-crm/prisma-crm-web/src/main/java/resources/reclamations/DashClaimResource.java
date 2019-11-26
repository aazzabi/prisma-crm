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
import Entities.User;
import Entities.Claim;
import Enums.ClaimStatus;
import Enums.ClaimType;
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
		if (UserService.UserLogged != null) {
			Role r = UserService.UserLogged.getRole();
			if (r == Role.Admin) {
				return Response.status(Status.CREATED).entity(cs.getAll()).build();
			} else if (r == Role.Client) {
				return Response.status(Status.CREATED).entity(cs.getClaimsByClient((Client) UserService.UserLogged))
						.build();
			} else if ((r == Role.financial) || (r == Role.technical) || (r == Role.relational)) {
				return Response.status(Status.CREATED).entity(cs.getClaimsByResponsable((Agent) UserService.UserLogged))
						.build();
			} else {
				return Response.status(Status.NOT_FOUND).build();
			}
		} else {
			return Response.status(Status.CREATED).entity("{\"error\": \" You are not connected ! \"}").build();
		}
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaim(@PathParam(value = "id") int id) {
		Claim c = cs.getById(id);

		if ((UserService.UserLogged.getId() == c.getResponsable().getId())
				|| (UserService.UserLogged.getId() == c.getFirstResponsable().getId())) {
			System.out.println("open(c)");
			return Response.status(Status.CREATED).entity(cs.open(c)).build();
		} else if ((UserService.UserLogged.getRole() == Role.Admin)) {
			return Response.status(Status.CREATED).entity(c).build();
		} else if ((UserService.UserLogged.getId() == c.getCreatedBy().getId())) {
			return Response.status(Status.CREATED).entity(c).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("{\"error\": \"Oupss !! Vous ne pouvez pas accéder à cette réclamation ! \"}" ).build();
		}
	}

	@PUT
	@Path("/archiver/{id}")
	@RolesAllowed(Permissions = { Role.financial, Role.relational, Role.technical })
	@Produces(MediaType.APPLICATION_JSON)
	public Response archiverCalim(@PathParam(value = "id") int id) {
		Claim c = cs.getById(id);
		cs.changeStatus(c, ClaimStatus.FERME_SANS_SOLUTION);
		return Response.status(Status.OK).entity(c).build();
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteClaim(@PathParam(value = "id") int id) {
		if ((id != 0) & (cs.getById(id) != null)) {
			Claim c = cs.getById(id);
			if ((UserService.UserLogged.getId() == c.getResponsable().getId())
					|| (UserService.UserLogged.getId() == c.getFirstResponsable().getId())
					|| (UserService.UserLogged.getId() == c.getCreatedBy().getId())
					|| (UserService.UserLogged.getRole() == Role.Admin)) {
				noteService.deleteNotesByClaimId(id);
				return Response.status(Status.OK).entity(cs.deleteClaimById(id)).build();
			} else {
				return Response.status(Status.NOT_FOUND).entity("{\"error\": \"Oupss !! Vous ne pouvez pas supprimer à cette réclamation ! \"}" ).build();
			}
		} else {
			return Response.status(Status.NOT_FOUND).entity("{\"error\":\" Réclamation non existante \"}").build();
		}
	}

	@GET
	@Path("/type/{t}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaimByType(@PathParam(value = "t") ClaimType t) {
		if ((t == ClaimType.FINANCIERE) || (t == ClaimType.RELATIONNELLE) || (t == ClaimType.TECHNIQUE)) {
			return Response.status(Status.OK).entity(cs.getByType(t)).build();
		}
		return Response.status(Status.NOT_FOUND).entity("{\"error\":\" Type non reconu \"}").build();
	}

	@PUT
	@Path("/resolve/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Permissions = { Role.financial, Role.relational, Role.technical })
	public Response resolveClaim(@PathParam(value = "id") int id) throws Exception {
		if ((id != 0) & (cs.getById(id) != null)) {
			Claim c = cs.getById(id);
			User u = UserService.UserLogged;
			if ((u.getId() == c.getResponsable().getId()) || (u.getId() == c.getFirstResponsable().getId())) {
				return Response.status(Status.OK).entity(cs.resolve(c)).build();
			} else {
				return Response.status(Status.NOT_FOUND).entity("Cette reclamation n'est pas affécté à vous !").build();
			}
		} else {
			return Response.status(Status.NOT_FOUND).entity("{\"error\": \" Réclamation non existante \"}"  ).build();
		}
	}

	@GET
	@Path("/deleguer/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Permissions = { Role.financial, Role.relational, Role.technical })
	public Response deleguerClaim(@PathParam(value = "id") int id) throws Exception {
		if ((id != 0) & (cs.getById(id) != null)) {
			Claim c = cs.getById(id);
			return Response.status(Status.OK).entity(cs.deleguer(c)).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Claim does'nt exist").build();
		}
	}

	@GET
	@Path("/agent/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed(Permissions = { Role.financial, Role.relational, Role.technical, Role.Admin })
	public Response getAllClaimByAgent(@PathParam(value = "id") int id) {
		if (UserService.UserLogged != null) {
			Agent a = cs.getResponsableById(id);
			return Response.status(Status.CREATED).entity(cs.getClaimsByResponsable(a)).build();
		}
		return Response.status(Status.CREATED).entity("{\"error\": \" You are not connected ! \"}"  ).build();
	}

	// ******************
	// CRUD NOTE CLAIM
	// ******************

	@POST
	@Path("addNoteToClaim/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addNotesToClaim(@PathParam(value = "id") int id, NoteClaim nc) {
		Claim c = cs.getById(id);
		NoteClaim note = noteService.addNoteClaim(nc, c);
		return Response.status(Status.OK).entity(note).build();
	}

	@PUT
	@Path("editNoteClaim/{id}")
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

	@DELETE
	@Path("/deleteNote/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteNoteClaim(@PathParam(value = "id") int id) {
		if ((id != 0) & (noteService.getNoteById(id) != null)) {
			NoteClaim nc = noteService.getNoteById(id);
			if ((UserService.UserLogged.getId() == nc.getCreatedBy().getId())
					|| (UserService.UserLogged.getId() == nc.getClaim().getResponsable().getId())
					|| (UserService.UserLogged.getId() == nc.getClaim().getFirstResponsable().getId())
					|| (UserService.UserLogged.getRole() == Role.Admin)) {
				return Response.status(Status.OK).entity(noteService.deleteNote(nc)).build();
			} else {
				return Response.status(Status.NOT_FOUND).entity("Vous ne pouvez pas la supprimer !").build();
			}
		} else {
			return Response.status(Status.NOT_FOUND).entity("Commentaire non existant ").build();
		}
	}

}
