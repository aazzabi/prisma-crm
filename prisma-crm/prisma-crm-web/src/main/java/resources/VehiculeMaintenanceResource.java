package resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import Entities.Product;
import Entities.RepairRequest;
import Entities.Vehicule;
import Entities.VehiculeMaintenance;
import Enums.RepairStatus;
import Interfaces.IVehiculeMtRemote;

@Path("maintenance")
@RequestScoped
public class VehiculeMaintenanceResource {
	@EJB
	IVehiculeMtRemote vehiculeMtRemote;

	@POST
	@Path("/add/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)

	public Response addMaintenanceR(@PathParam(value = "id") int id, VehiculeMaintenance maintenance) {
		Vehicule xx = vehiculeMtRemote.getVehiculeById(id);

		if (xx != null) {
			maintenance.setRepairStatus(RepairStatus.OnHold);
			maintenance.setVehicule(xx);
			vehiculeMtRemote.addMaintanceRequest(maintenance);
			return Response.status(Status.CREATED).entity("Add").build();

		}
		else
		return Response.status(Status.BAD_GATEWAY).entity("Not").build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("find/{id}")
	public List<VehiculeMaintenance> vehiculeMByVeh(@PathParam("id") int id) {

		return vehiculeMtRemote.findMaintancebyVehicule(id);

	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("getAll")
	public List<VehiculeMaintenance> AllMaintenance() {

		return vehiculeMtRemote.findAll();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("approve/{id}")
	public Response approveMaintance(@PathParam("id") int id) {
		vehiculeMtRemote.ApproveMaintance(id);
		return Response.status(Status.OK).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("reject/{id}")
	public Response RejectMaintance(@PathParam("id") int id) {
		vehiculeMtRemote.RejectMaintance(id);
		return Response.status(Status.CREATED).entity("ok").build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("findMostMaintained")
	public Response MostMaintainedVehicule() throws Exception {

		return Response.status(Status.CREATED).entity(vehiculeMtRemote.findMostMaintainedVehicule()).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("NearEntreiens")
	public List<VehiculeMaintenance> findAllNearEntreiens() throws Exception {

		return vehiculeMtRemote.alertEntreiens();

	}

	@DELETE
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteVehiculeMaint(@PathParam(value = "id") int id) {
		vehiculeMtRemote.deleteVehiculeMt(id);
		return "ok";
	}

}
