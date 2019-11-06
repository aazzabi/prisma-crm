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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import Enums.ClaimType;
import Enums.Role;
import Services.ClaimService;
import Services.NoteClaimService;
import utilities.RolesAllowed;
import utilities.Secured;
import Entities.Claim;
import javax.ejb.EJB;

@Path("claim")
public class ClaimResource {

	@EJB
	public static ClaimService cs = new ClaimService();
	
	@EJB
	public static NoteClaimService noteService = new NoteClaimService();

	@POST
	@Path("new")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addClaim(Claim c) throws Exception {
		c.setId(cs.addClaim(c));
		return Response.status(Status.CREATED).entity(c).build();
	}
	
	//@RolesAllowed(Permissions = {Role.financial})
	@GET
	@Path("getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllClaim() {
		return Response.status(Status.CREATED).entity(cs.getAll()).build();
	}
	
	@GET
	//@RolesAllowed(Permissions = {Role.technical})
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

	@PUT
	@Path("addFAQ/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setClaimAsFaq(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(cs.addClaimToFaq(id)).build();
	}

	@GET
	@Path("/id/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaimByID(@PathParam(value = "id") int id) {
		if (id !=0) {
			return Response.status(Status.CREATED).entity(cs.getById(id)).build();
		} else {
			return Response.status(Status.NOT_FOUND).build();
		}
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
	

	@GET
	@Path("/agent/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllClaimByAgent(@PathParam(value="id") int id) {
		Agent a = cs.getResponsableById(id);
		return Response.status(Status.CREATED).entity(cs.getClaimsByResponsable(a)).build();
	}

	@PUT
	@Path("/editClaim/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateClaim(@PathParam(value = "id") int id, Claim c) {
		Claim cl = cs.getById(id);
		Claim injecter = c;
		injecter.setId(cl.getId());
		if (injecter.getStatus() == null) {
			injecter.setStatus(cl.getStatus());
		}
		if (injecter.getPriority() == null) {
			injecter.setPriority(cl.getPriority());
		}
		if (injecter.getType() == null) {
			injecter.setType(cl.getType());
		}
		if (injecter.getResponsable() == null) {
			injecter.setResponsable(cl.getResponsable());
		}
		if (injecter.getFirstResponsable() == null) {
			injecter.setFirstResponsable(cl.getFirstResponsable());
		}
		if (injecter.getResolvedAt() == null) {
			injecter.setResolvedAt(cl.getResolvedAt());
		}
		if (injecter.getCode() == null) {
			injecter.setCode(cl.getCode());
		}
		if (injecter.getTitle() == null) {
			injecter.setTitle(cl.getTitle());
		}
		if (injecter.getOpenedAt() == null) {
			injecter.setOpenedAt(cl.getOpenedAt());
		}
		if (injecter.getCreatedBy() == null) {
			injecter.setCreatedBy(cl.getCreatedBy());
		}
		if (injecter.getCreatedAt() == null) {
			injecter.setCreatedAt(cl.getCreatedAt());
		}
		if (injecter.getResolvedBy() == null) {
			injecter.setResolvedBy(cl.getResolvedBy());
		}
		if (injecter.getDescription() == null) {
			injecter.setDescription(cl.getDescription());
		}
		if (injecter.getDelegatedAt() == null) {
			injecter.setDelegatedAt(cl.getDelegatedAt());
		}
		injecter.setIsFaq(cl.getIsFaq());

		Claim newC = (Claim) cs.merge(injecter);
		return Response.status(Status.OK).entity(newC).build();
	}

	@PUT
	@Path("/archiverClaim/{id}")
	public Response archiverCalim(@PathParam(value = "id") int id) {
		Claim newC = cs.getById(id);
		cs.changeStatus(newC, ClaimStatus.FERME_SANS_SOLUTION);
		return Response.status(Status.OK).build();
	}
	
	@DELETE
	@Path("/delete/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteClaim(@PathParam(value = "id") int id) {
		noteService.deleteNotesByClaimId(id);
		return Response.status(Status.OK).entity(cs.deleteClaimById(id)).build();
	}


	@GET
	@Path("/type/{t}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaimByType(@PathParam(value = "t") ClaimType t) {
		List<Claim> l = cs.getByType(t);
		Claim c = cs.getById(1);
		return Response.status(Status.OK).entity(cs.getByType(t)).build();
	}
	
	/***** TEST *****/
	@GET
	@Path("/deleguer/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleguerClaim(@PathParam(value = "id") int id) throws Exception {
		Claim c = cs.getById(id);
		return Response.status(Status.OK).entity(cs.deleguer(c)).build();
	}
	
	@GET
	@Path("/open/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testOpenClaim(@PathParam(value = "id") int id) throws Exception {
		Claim c = cs.getById(id);
		return Response.status(Status.OK).entity(cs.open(c)).build();
	}
	
	@GET
	@Path("/resolve/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response testResolveClaim(@PathParam(value = "id") int id) throws Exception {
		Claim c = cs.getById(id);
		return Response.status(Status.OK).entity(cs.resolve(c)).build();
	}

	/*
	@GET
	@Path("/freqWord/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response freqWordClaim(@PathParam(value = "id") int id) throws Exception {
		Claim c = cs.getById(id);
		//return Response.status(Status.OK).entity(cs.extractKeyWords(c)).build();
		return Response.status(Status.OK).entity(cs.getKeyWords()).build();
	}
	*/
	
	/*
	 * AFFICHAGE DE DATE
		System.out.println("Minutes :" + (c.getResolvedAt().getTime() - c.getCreatedAt().getTime()) / (1000 * 60));
		System.out.println("Hours :" + (c.getResolvedAt().getTime() - c.getCreatedAt().getTime()) / (1000 * 60 * 60));
		System.out.println(cs.calculMoyTemp(5, 2, c.getCreatedAt(), c.getResolvedAt()));
		// return Response.status(Status.OK).entity(cs.calculMoyTemp(5, 2, new
		// Date(2011,11,31,23,59), new Date(System.currentTimeMillis()))).build();
	*/
}
