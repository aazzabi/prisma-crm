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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import Entities.Store;
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
	@Path("/find/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findStoreById(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(serv.findStoreById(id)).build();

	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateStore(Store newStore) {

		return Response.status(Status.OK).entity(serv.updateStore(newStore)).build();

	}

	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteStore(@PathParam(value = "id") int id) {
		serv.removeStore(id);

		return Response.status(Status.OK).entity("deleted").build();
	}
	
}
