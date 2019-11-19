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

import java.io.IOException;
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
	public Response addClaim(Claim c) throws Exception {
		if (UserService.UserLogged != null) { 
			c.setCreatedBy((Client)UserService.UserLogged);
			c.setId(cs.addClaim(c));
		return Response.status(Status.CREATED).entity(c).build();
		} else {
			return Response.status(Status.NOT_FOUND).entity("Veuillez se connecter d'abord").build();				
		}
	}
	
	@GET
	@Path("/code/{c}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getClaimByCode(@PathParam(value = "c") String c) {
		if ((c!="") && ( cs.getByCode(c)!=null )){ 
			Claim claim = cs.getByCode(c);
			String status="";
			if (claim.getStatus()== ClaimStatus.EN_ATTENTE) { status = "Veuillez nous excuser, votre réclamation est en attente";}
			else if (claim.getStatus()== ClaimStatus.EN_COURS) { status = "Votre réclamation est en cours de résolution";}
			else if (claim.getStatus()== ClaimStatus.RESOLU) { status = "Votre réclamation est bien traité, veuillez se connecter pour plus de détails ";}
			else if (claim.getStatus()== ClaimStatus.FERME_SANS_SOLUTION) { status = "Ouuppss !! on a besoin de plus de détails ,veuillez se connecter pour nous fournir plus de détails ! ";}

			return Response.status(Status.OK).entity(status).build();
		}
		return Response.status(Status.NOT_FOUND).entity("Code invalide").build();		
	}	
	
	@GET
	@Path("FAQ")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllFaq() throws IOException {
		cs.updateFAQ();
		return Response.status(Status.CREATED).entity(cs.getAllFaq()).build();
	}
	
	@GET
	@Path("FAQ/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFaqById(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(cs.getAllFaq().get(id-1)).build();
	}
		
	@GET
	@Path("fwc")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFwc() throws IOException {
		return Response.status(Status.OK).entity(cs.getFwc()).build();
	}
	
	@GET
	@Path("fwac")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getFwac() throws IOException {
		return Response.status(Status.OK).entity(cs.getFwac()).build();
	}
	
	@PUT
	@Path("updateFaqq")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateFAQ() throws IOException {
		cs.updateFAQ();
		return Response.status(Status.OK).entity("updated").build();
	}

}
