package resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import Entities.Agent;
import Entities.User;
import Enums.AccountState;
import Enums.Role;
import Interfaces.IUserLocal;
import Services.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import utilities.*;

@Path("users")
@RequestScoped
public class UserResource {

	@EJB
	IUserLocal userBusiness;
	final ObjectMapper mapper = new ObjectMapper();

	@Context
	private UriInfo uriInfo;

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("email") String email, @FormParam("password") String password) {

		try {
			User u = authenticate(email, password);
			Role aRole = u.getRole();
			if (u != null) {
				if (u.getAccountState() != AccountState.ACTIVATED)
					return Response.status(Response.Status.NOT_ACCEPTABLE)
							.entity("Your account is inactive , Please check your Email and confirm your Account !")
							.build();
				else {
					String role = null;
					if (u.getRole() == Role.Client) {
						role = "Client";
					} else if (u.getRole() == Role.financial) {
						Agent emp = (Agent) u;
						role = emp.getRole().toString();
						System.out.println(role);
					} else if (u.getRole() == Role.technical) {
						Agent emp = (Agent) u;
						role = emp.getRole().toString();
						System.out.println(role);
					} else if (u.getRole() == Role.relational) {
						Agent emp = (Agent) u;
						role = emp.getRole().toString();
						System.out.println(role);
					} else if (u.getRole() == Role.Driver) {
						Agent emp = (Agent) u;
						role = emp.getRole().toString();
						System.out.println(role);
					}
					String token = issueToken(Integer.toString(u.getId()), role);
					System.out.println("token : " + token);
					return Response.ok(token).build();
				}

			}
			return Response.status(Response.Status.FORBIDDEN).build();
		} catch (Exception e) {
			return Response.status(Response.Status.FORBIDDEN).build();
		}
	}

	private User authenticate(String username, String password) {
		User u = userBusiness.loginUser(username, password);
		return u;
	}

	private String issueToken(String username, String role) {
		String keyString = "simplekey";
		Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
		String jwtToken = Jwts.builder().setSubject(username).claim("Role", role)
				.setIssuer(uriInfo.getAbsolutePath().toString()).setIssuedAt(new Date())
				.setExpiration(toDate(LocalDateTime.now().plusMinutes(15L))).signWith(SignatureAlgorithm.HS512, key)
				.compact();
		return jwtToken;
	}

	private Date toDate(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	public Claims decodeToken(String token, Key key) {
		try {
			Claims jwsMap = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
			return mapper.convertValue(jwsMap, Claims.class);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			return null;
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("register")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(User user) {

		return Response.status(Status.OK).entity(userBusiness.createUser(user)).build();
	 
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

	// @Secured
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{oldPwd}/{newPwd}")
	public Response changePwd(User user, @PathParam("oldPwd") String oldPwd, @PathParam("newPwd") String newPwd) {
		if (userBusiness.changePwd(user, oldPwd, newPwd))
			return Response.status(Status.OK).entity(true).build();
		else {
			return Response.status(Status.FORBIDDEN).entity(false).build();
		}
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("assignClient/{id}")
	public Response AddClient(@PathParam("id") int id) {

	
		return Response.status(Status.FOUND).entity(userBusiness.AssignClient(id)).build();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("assignAgent/{id}")
	public Response AddAgent(@PathParam("id") int id) {

	
		return Response.status(Status.FOUND).entity(userBusiness.AssignAgents(id)).build();

	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("logout")
	public Response logout() {

		UserService.UserLogged = null;
		issueToken(null, null);
		return Response.status(Status.NOT_FOUND).entity(false).build();

	}
}