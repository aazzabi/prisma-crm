package utilities;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Key;
import java.util.Arrays;
import javax.annotation.Priority;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import Enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@RolesAllowed
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthTokenFilter implements ContainerRequestFilter {

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		try {
			String keyString = "simplekey";
			Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
			if (authorizationHeader!="") {
				String token = authorizationHeader.substring("Bearer".length()).trim();
				Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
	
				Method method = resourceInfo.getResourceMethod();
				if (method != null) {
					RolesAllowed JWTContext = method.getAnnotation(RolesAllowed.class);
					Role[] permission = JWTContext.Permissions();
	
					if (!permission.equals(Role.NoRights)) {
						String roles = claims.get("Role", String.class);
						Role roleUser = Role.valueOf(roles);
						System.out.println("User Role : " + roleUser.toString());
						System.out.println("Roles permitted (st): " + permission.toString());
						boolean contains = Arrays.stream(permission).anyMatch(role -> role.equals(roleUser));
						if (!contains)
							requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
					}
				}
			} else { requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());}

		} catch (Exception e) {
			e.printStackTrace();
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}

	}
/*
	private String issueToken(User user) {
		// Issue a token (can be a random String persisted to a database or a JWT token)
		// The issued token must be associated to a user
		// Return the issued token
		String keyString = "simplekey";
		Key key = new SecretKeySpec(keyString.getBytes(), 0, keyString.getBytes().length, "DES");
		System.out.println("the key is : " + key.hashCode());
		System.out.println("uriInfo.getAbsolutePath().toString() : " + uriInfo.getAbsolutePath().toString());
		System.out.println("Expiration date: " + toDate(LocalDateTime.now().plusMinutes(15L)));
		System.out.println("role: "+ user.getClass().getSimpleName());
		String jwtToken = Jwts.builder().setSubject(user.getEmail()).claim("role", user.getRole()).setIssuer(uriInfo.getAbsolutePath().toString())
				.setIssuedAt(new Date()).setExpiration(toDate(LocalDateTime.now().plusMinutes(15L)))
				.signWith(SignatureAlgorithm.HS512, key).compact();
		System.out.println("the returned token is : " + jwtToken);
		return jwtToken;
	}

*/	
}