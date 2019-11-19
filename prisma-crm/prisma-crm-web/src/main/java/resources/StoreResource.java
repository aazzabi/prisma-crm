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

import Entities.Store;
import Entities.StoreHours;
import Interfaces.IStoreServiceLocal;

@Path("/store")
public class StoreResource {

	@EJB
	IStoreServiceLocal serv;
	
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addStore(Store s) {
		Store st = serv.addStore(s);
		return Response.status(Status.CREATED).entity(st).build();
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allStores() {
		return Response.status(Status.CREATED).entity(serv.findAllStores()).build();

	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findStoreById(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(serv.findStoreById(id)).build();

	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStore(Store newStore,@QueryParam(value="idAddress")int idAddress) {

		newStore.setAddress(serv.findAdrById(idAddress));
		return Response.status(Status.OK).entity(serv.updateStore(newStore)).build();

	}

	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStore(@PathParam(value = "id") int id) {
		serv.removeStore(id);

		return Response.status(Status.OK).entity("deleted").build();
	}
	
	
	@POST
	@Path("/hours/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addStoreHour(StoreHours s) {
		StoreHours sh = serv.addStoreTime(s);
		return Response.status(Status.CREATED).entity(sh).build();
	}

	@GET
	@Path("/hours/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allStoreHours() {
		return Response.status(Status.CREATED).entity(serv.findAllStoreTimes()).build();

	}

	@GET
	@Path("/hours/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findStoreHourById(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(serv.findStoreTimeById(id)).build();

	}

	@PUT
	@Path("/hours")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStoreHour(StoreHours newSH, @QueryParam(value="idStore")int idStore, @QueryParam(value="idTime")int idTime) {

		if(idStore!=0 && idTime!=0) {
			serv.assignTimeToStore(idStore, idTime);
			Store s = serv.findStoreById(idStore);
			return Response.status(Status.CREATED).entity(s).build();
		}
		return Response.status(Status.OK).entity(serv.updateStoreTime(newSH)).build();

	}

	@DELETE
	@Path("/hours/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStoreHour(@PathParam(value = "id") int id) {
		serv.removeStoreTime(id);

		return Response.status(Status.OK).entity("deleted").build();
	}

	
	@Path("/getNearesAddress/{lon}/{lat}")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getNearestAddress(@PathParam(value="lon") double lon,@PathParam(value="lat") double lat)
	{
		
		return serv.getNearestStoreAddress(lon,lat).getName();
		//return cartService.reverseGeoCode(lon, lat);
	}

	
}
