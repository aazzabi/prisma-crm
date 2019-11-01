package resources;

import java.util.ArrayList;
import java.util.List;

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

import Entities.Pack;
import Interfaces.IPack;




@Path("/pack")
public class PackResource {
	@EJB
    IPack pac ;
	
	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProduct(Pack p) {
		Pack pa = pac.addpack(p);
		return Response.status(Status.CREATED).entity(pa).build();
	}
	
	@DELETE
	@Path("/delete/{idp}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePack( @PathParam(value="idp")int idp) {
	 pac.deletePack(idp);
		return "deleted";
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updatepack(Pack pack) {

	
	return Response.status(Status.OK).entity(pac.updatepack(pack)).build();

	}
	
	@POST
	@Path("/addproductpack/{idp}/{idpa}")
	@Produces(MediaType.APPLICATION_JSON)
    public Response addproductpack(@PathParam(value="idp")int idp,@PathParam(value="idpa")int idpa) {
	 pac.addproductpack(idp, idpa);
	return Response.status(Status.OK).build();
	}
	@DELETE
	@Path("/deleteproductpack/{idp}/{idpa}")
	@Produces(MediaType.APPLICATION_JSON)
    public Response deleteproductpack(@PathParam(value="idp")int idp,@PathParam(value="idpa")int idpa) {
	 pac.deleteproductpack(idp, idpa);
	return Response.status(Status.OK).build();
	}
	
	
	@GET
	@Path("/allProduct/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allProduct(@PathParam(value = "id") int id) {
		List<Object> list = new ArrayList<Object>();
		list.add(pac.findpack(id));
		
		list.add(pac.getAllProductPerPack(id));
		return Response.status(Status.CREATED).entity(list).build();
	}

}
