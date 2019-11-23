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

}