package resources;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
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
import Entities.Store;
import Interfaces.IProductServiceLocal;
import Interfaces.IRepaiRequest;

@Path("Repair")
public class RepairResource {
	@EJB
	IRepaiRequest repaiRequest;

	@EJB
	IProductServiceLocal ps;

	
	@PUT
	@Path("/add/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	
	public Response addRepair(@PathParam(value = "id") int id) {
		Product xx= ps.findProductById(id);
		RepairRequest x = new RepairRequest();
		if (xx != null)
		{
			System.out.println("Connectedddd :" +UserResource.getUserConnected());
			x.setClient(UserResource.getUserConnected());
			x.setCreatedDate(getDateNow());
			x.setProduct(xx);
			x.setStatusRep(Enums.RepairStatus.OnHold);
			repaiRequest.createRepairRequest(x);

		}
		
		
		return Response.status(Status.CREATED).build();
	}

	@GET
	@Path("/all")
	@Produces(MediaType.APPLICATION_JSON)
	public Response allRepairRequest() {
		return Response.status(Status.CREATED).entity(repaiRequest.getAllRepairRequest()).build();

	}
	public Date getDateNow() {
		SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String pattern = "yyyy-MM-dd HH:mm:ss.SSS";

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("fr", "FR"));

		String date = simpleDateFormat.format(new Date());
		System.out.println("DATE " + date);

		java.sql.Timestamp sqlTimestamp = java.sql.Timestamp.valueOf(date);
		java.util.Date improperUtilDate = sqlTimestamp;

		return improperUtilDate;
	}

}
