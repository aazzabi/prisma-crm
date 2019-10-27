package resources;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import Entities.Claim;
//import javax.ejb.EJB;

@Path("msg")
@RequestScoped
public class MessageResource 
{
	@GET
	public String SayHello()
	{
		String S = "Hello";
		return S;
	}

}
