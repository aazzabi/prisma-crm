package Utils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

public class AuthJWT {

	final static String issuer = "https://prisma-crm.tn/";
	final static String secret = "123456";
	public static String SignJWT(String nameOfObject,Object theObject) {
		

		final long iat = System.currentTimeMillis() / 1000L; // issued at claim 
		final long exp = iat + 10800L; // expires claim. In this case the token expires in 5 Min

		final JWTSigner signer = new JWTSigner(secret);
		final HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put("iss", issuer);
		claims.put("exp", exp);
		claims.put("iat", iat);
		claims.put(nameOfObject, theObject);

		final String jwt = signer.sign(claims);
		return jwt;
	}
	
	
	public static Map<String, Object> VerifyJWT(String jwtToVerify)
	{
		
		try {
		    final JWTVerifier verifier = new JWTVerifier(secret);
		    final Map<String, Object> claims= verifier.verify(jwtToVerify);
		    System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n Verification \n\n : " +claims+"\n\n END \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		   // return claims;
		    return claims;
		} catch (JWTVerifyException | InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException | IOException e) {
		    // Invalid Token
			//return null;
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n Verification \n\n PROBLEM \n\n END \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		
			return null;
		
		}
		
		
	}

}
