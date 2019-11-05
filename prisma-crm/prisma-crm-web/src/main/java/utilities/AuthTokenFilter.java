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

		} catch (Exception e) {
			e.printStackTrace();
			requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
		}

	}

}