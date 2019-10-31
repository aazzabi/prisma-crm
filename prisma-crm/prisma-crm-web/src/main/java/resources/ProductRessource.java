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

import Entities.Mobile;
import Entities.Product;
import Entities.Store;
import Entities.Tariff;
import Interfaces.IProductServiceLocal;
import Interfaces.IStoreServiceLocal;



@Path("/product")
public class ProductRessource {

	@EJB
	IProductServiceLocal ps;
	@EJB
	IStoreServiceLocal ss;

	@POST
	@Path("/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addProduct(Product p) {
		Product pr = ps.addProduct(p);
		return Response.status(Status.CREATED).entity(pr).build();
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allProducts() {
		return Response.status(Status.CREATED).entity(ps.findAllProducts()).build();

	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findProductById(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(ps.findProductById(id)).build();
	}
	
	@GET
	@Path("/ref/{ref}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findProductByRef(@PathParam(value = "ref") String ref) {
		return Response.status(Status.CREATED).entity(ps.findProductByReference(ref)).build();
	}
	
	@GET
	@Path("/store/{idStore}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findProductByStore(@PathParam(value = "idStore") int idStore) {
		Store st= ss.findStoreById(idStore);
		return Response.status(Status.CREATED).entity(ps.findProductsByStore(st)).build();
	}


	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProduct(Product newProduct, @QueryParam(value="idProduct")int idProduct, @QueryParam(value="idTarif")int idTarif) {

		if(idProduct!=0 && idTarif!=0) {
			ps.assignTarifToProduct(idProduct, idTarif);
			return Response.status(Status.CREATED).entity(ps.findProductById(idProduct)).build();
		}
		return Response.status(Status.OK).entity(ps.updateProduct(newProduct)).build();

	}

	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteProduct(@PathParam(value = "id") int id) {
		ps.removeProduct(id);

		return Response.status(Status.OK).entity("deleted").build();
	}
	
	
	
	@POST
	@Path("/tarif/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addTarif(Tariff t) {
		Tariff tr = ps.addTarif(t);
		return Response.status(Status.CREATED).entity(tr).build();
	}

	@GET
	@Path("/tarif/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allTarifs() {
		return Response.status(Status.CREATED).entity(ps.findAllTarifs()).build();

	}

	@GET
	@Path("/tarif/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findTarifById(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(ps.findTarifById(id)).build();

	}

	@PUT
	@Path("/tarif")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateTarif(Tariff newTariff) {

		return Response.status(Status.OK).entity(ps.updateTarif(newTariff)).build();

	}

	@DELETE
	@Path("/tarif/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteTarif(@PathParam(value = "id") int id) {
		ps.removeTarif(id);

		return Response.status(Status.OK).entity("deleted").build();
	}
	
	@POST
	@Path("/mobile/add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addMobile(Mobile m) {
		Mobile mb = ps.addMobile(m);
		return Response.status(Status.CREATED).entity(mb).build();
	}
	
	@PUT
	@Path("/mobile")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateMobile(Mobile newMobile) {

		return Response.status(Status.OK).entity(ps.updateMobile(newMobile)).build();

	}

}
