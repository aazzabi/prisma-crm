package resources;


import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import Entities.Agent;
import Entities.Client;
import Entities.User;
import Enums.AccountState;
import Enums.Role;
import Interfaces.IUserLocal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
//import services.LoginServiceImpl;

@Path("login")
public class AuthenticationEndPoint {
/*
	@Inject
	LoginServiceImpl loginService;
*/
	@EJB
	IUserLocal userBusiness;
	
	final ObjectMapper mapper = new ObjectMapper();

	@Context
	private UriInfo uriInfo;

	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("email") String email, @FormParam("password") String password) {
		
		try {
			User u = authenticate(email, password);
			Role aRole = u.getRole();
			if (u != null) {
				if (u.getAccountState()!= AccountState.ACTIVATED)
					return Response.status(Response.Status.NOT_ACCEPTABLE)
							.entity("Your account is inactive , Please check your Email and confirm your Account !")
							.build();
				else {
					String role = null;
					if (u.getRole()== Role.Client) {
						role = "Client";
					} else if (u.getRole()== Role.financial) {
						Agent emp = (Agent) u;
						role = emp.getRole().toString();
						System.out.println(role);
					} else if (u.getRole()== Role.technical) {
						Agent emp = (Agent) u;
						role = emp.getRole().toString();
						System.out.println(role);
					} else if (u.getRole()== Role.relational) {
						Agent emp = (Agent) u;
						role = emp.getRole().toString();
						System.out.println(role);
					} else if (u.getRole()== Role.Driver) {
						Agent emp = (Agent) u;
						role = emp.getRole().toString();
						System.out.println(role);
					}
					String token = issueToken(Integer.toString(u.getId()), role);
					System.out.println("token : "+token);
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
		System.out.println("the key is : " + key.hashCode());
		System.out.println("uriInfo.getAbsolutePath().toString() : " + uriInfo.getAbsolutePath().toString());
		System.out.println("Expiration date: " + toDate(LocalDateTime.now().plusMinutes(15L)));
		String jwtToken = Jwts.builder().setSubject(username).claim("Role", role)
				.setIssuer(uriInfo.getAbsolutePath().toString()).setIssuedAt(new Date())
				.setExpiration(toDate(LocalDateTime.now().plusMinutes(15L))).signWith(SignatureAlgorithm.HS512, key)
				.compact();
		System.out.println("the returned token is : " + jwtToken);
		System.out.println(decodeToken(jwtToken, key));
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

}