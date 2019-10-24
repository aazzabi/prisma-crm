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

import Entities.Product;
import Entities.Tariff;
import Interfaces.IProductServiceLocal;
import Services.ProductService;


@Path("/product")
public class ProductRessource {

	@EJB
	IProductServiceLocal ps;

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
	@Path("/find/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findProductById(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(ps.findProductById(id)).build();

	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateProduct(Product newProduct) {

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
	@Path("/tarif/find/{id}")
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
}
