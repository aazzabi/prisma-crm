package resources;

import java.util.List;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import Entities.Agent;
import Entities.User;
import Entities.Vehicule;
import Interfaces.IResourcesRemote;
import utilities.Secured;
import utilities.SessionUtils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("Resources")
@RequestScoped
public class VehiculeResource {
	private Agent user;

	@EJB
	IResourcesRemote resourcesRemote;


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("add")
	public void addVehicule(Vehicule vehicule) {

		resourcesRemote.addVehicule(vehicule);

	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Vehicule vehiculeById(@PathParam("id") int id) {
        System.out.println("#######*************##"+UserResource.getUserConnected().getId()+  "####************#######");

		return resourcesRemote.getVehiculeById(id);
		
	}
	
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response updateAnswer(Vehicule vehicule,@PathParam(value="id")int id){
        Vehicule x = resourcesRemote.getVehiculeById(id);

		try {
			resourcesRemote.updateVehicule(x);
			return Response.status(Status.OK).build();
		} catch (Exception e) {
			return Response.status(Status.NOT_MODIFIED).build();
		}

	}
	
 

		@PUT
	    @Path("update/{id}")
	    public Response update(@PathParam("id") int id, Vehicule vehicule) {
            Vehicule x = resourcesRemote.getVehiculeById(id);
	        x.setFuelType(vehicule.getFuelType());
	        x.setDriver(UserResource.getUserConnected());
	        System.out.println("#######*************##"+UserResource.getUserConnected().getEmail()+  "####************#######");
	        x.setLocation(vehicule.getLocation());
	        x.setOdometer(vehicule.getOdometer());
	        x.setPlate(vehicule.getPlate());
	        resourcesRemote.updateVehicule(x);


	        return Response.ok().build();
	    }

		@Secured
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		@Path("All")
		public List<Vehicule> findAllVehicules() {

			return resourcesRemote.findAllVehicule();
		}

		@DELETE
		@Path("{id}")
		public void deleteVehicule(@PathParam("id") int id) {

			resourcesRemote.deleteVehicule(id);

		}
}
