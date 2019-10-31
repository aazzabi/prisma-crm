package resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import Entities.Agent;
import Entities.User;
import Interfaces.IUserLocal;
import utilities.*;

@Path("users")
@RequestScoped
public class UserResource {
    
	@EJB
	IUserLocal userBusiness;

	public static User userConnected;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("register")
	public void createUser(User user) {

		userBusiness.createUser(user);

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("confirm/{confirmationToken}")
	public Response activateAccount(@PathParam("confirmationToken") String confirmationToken) {
		boolean result = userBusiness.activateAccount(confirmationToken);
		if (result) {
			return Response.status(Status.OK).entity(true).build();
		} else {
			return Response.status(Status.FORBIDDEN).entity(false).build();
		}

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public User findUserById(@PathParam("id") int id) {

		return userBusiness.findUserById(id);
	}

	@Secured
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("All")
	public List<User> findAllUsers() {
        System.out.println("#######*************##"+ SessionUtils.USER_LAST_NAME+  "####************#######");

		return userBusiness.findAllUsers();
	}

	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updateUser(User user, @PathParam(value = "id") int id) {
		String imgToUpload = user.getProfileImage();
		if (imgToUpload != null) {
			if (userBusiness.uploadProfileImage(imgToUpload)) {
				if (user.getId() == 0) {
					user.setId(id);
				}
				userBusiness.updateUser(user);
				return Response.status(Status.OK).entity(true).build();
			} else {
				if (user.getId() == 0) {
					user.setId(id);
				}
				userBusiness.updateUser(user);
				return Response.status(Status.FORBIDDEN).entity(false).build();
			}
		}

		userBusiness.updateUser(user);
		return Response.status(Status.OK).entity("Problem not image changed").build();

	}
	@PUT
	@Path("agent/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response updatezUser(Agent agent, @PathParam(value = "id") int id) {
		String imgToUpload = agent.getProfileImage();
		if (imgToUpload != null) {
			if (userBusiness.uploadProfileImage(imgToUpload)) {
				if (agent.getId() == 0) {
					agent.setId(id);
				}
				userBusiness.updateUser(agent);
				return Response.status(Status.OK).entity(true).build();
			} else {
				if (agent.getId() == 0) {
					agent.setId(id);
				}
				userBusiness.updateUser(agent);
				return Response.status(Status.FORBIDDEN).entity(false).build();
			}
		}

		userBusiness.updateUser(agent);
		return Response.status(Status.OK).entity("Problem not image changed").build();

	}
	@DELETE
	@Path("{id}")
	public void deleteUser(@PathParam("id") int id) {

		userBusiness.deleteUser(id);

	}

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{email}/{pwd}")
	public String loginUser(@PathParam("email") String email, @PathParam("pwd") String pwd) {
		User userLogged = userBusiness.loginUser(email, pwd);
         
         UserResource.setUserConnected(userLogged);
         
		return userLogged.getEmail();
		
	}
	
	//@Secured
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{oldPwd}/{newPwd}")
	public Response changePwd(User user,@PathParam("oldPwd") String oldPwd, @PathParam("newPwd") String newPwd) {
		if(userBusiness.changePwd(user,oldPwd, newPwd))
		return Response.status(Status.OK).entity(true).build();
		else
		{
			return Response.status(Status.FORBIDDEN).entity(false).build();
		}
	}

	public static User getUserConnected() {
		return userConnected;
	}

	public static void setUserConnected(User userConnected) {
		UserResource.userConnected = userConnected;
	}

	


}