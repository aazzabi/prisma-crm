package resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.security.RolesAllowed;
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

import com.auth0.jwt.internal.org.apache.commons.lang3.time.DateUtils;

import Entities.Invoice;
import Entities.Pack;
import Entities.Product;
import Entities.RepairRequest;
import Entities.Store;
import Enums.RepairStatus;
import Enums.Role;
import Interfaces.IInvoiceRemote;
import Interfaces.IProductServiceLocal;
import Interfaces.IRepaiRequest;
import Interfaces.IUserRemote;
import Services.UserService;

@Path("Repair")
@RequestScoped
public class RepairResource {
	@EJB
	IRepaiRequest repaiRequest;
	@EJB
	IProductServiceLocal ps;

		
	@PUT
	@Path("/add/{idinv}/{idprod}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addRepair(@PathParam(value = "idinv") int idinv, @PathParam(value = "idprod") int idprod,
			RepairRequest x) {
		Invoice check = repaiRequest.findInvoiceById(idinv);
		Product p = repaiRequest.findProductByinvoice(idprod, idinv);
		if (check != null & p != null) {
			Date WarrentyExp = DateUtils.addYears(check.getCreatedAt(), p.getGuarantee());
			if (WarrentyExp.after(new Date())) {
				x.setClient(UserService.UserLogged);
				x.setCreatedDate(new Date());
				x.setStatusRep(Enums.RepairStatus.OnHold);
				x.setWarentyExp(WarrentyExp);
				x.setInvoice(check);
				repaiRequest.createRepairRequest(x);
				return Response.status(Status.CREATED).build();

			} else {
				return Response.status(Response.Status.NOT_FOUND).entity("Warrenty expired").build();

			}
		}
		return Response.status(Status.NOT_FOUND).build();

	}

	@GET
	@Path("/all")
	@utilities.RolesAllowed(Permissions = {Role.technical})
	@Produces(MediaType.APPLICATION_JSON)
	public Response allRepairRequest() {
		return Response.status(Status.CREATED).entity(repaiRequest.getAllRepairRequest()).build();

	}

	@GET
	@Path("/check/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response checkRepairRequest(@PathParam(value = "id") int id) {
		return Response.status(Status.CREATED).entity(repaiRequest.findRepairRequest(id)).build();

	}

	@PUT
	@Path("/update/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateRepReq(@PathParam(value = "id") int id, RepairRequest reprequest) {

		return Response.status(Status.OK).entity(repaiRequest.updateRepairRequest(id, reprequest)).build();

	}

	@PUT
	@Path("/status/{id}")
	public Response RepRequestStatus(@PathParam(value = "id") int id, RepairRequest xx) {

		return Response.status(Status.OK).entity(repaiRequest.RepairRequestStatus(id, xx)).build();

	}

	@DELETE
	@Path("/delete/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteRepRequest(@PathParam(value = "id") int id) {
		repaiRequest.deleteRepairRequest(id);
		return "deleted";
	}

	@GET
	@Path("find/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findrep(@PathParam(value = "id") int id) {

		return Response.status(Status.OK).entity(repaiRequest.findRepairRequest(id)).build();
	}
	@GET
	@Path("findSentiment")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findrep() throws Exception {

		return Response.status(Status.OK).entity(repaiRequest.findSentiment()).build();
	}
	@PUT
	@Path("/check/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response Comment(@PathParam(value = "id") int id, RepairRequest s) {

		return Response.status(Status.OK).entity(repaiRequest.ReviewAdd(s.getReview(), id)).build();

	}

}
