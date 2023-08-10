package com.klaus.saas.system.server.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.klaus.saas.system.server.config.JwtProperties;
import com.klaus.saas.system.server.constants.AuthConstants;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author shilin
 * @since 2019-09-06
 */
@Component
public class JwtUtils {

	private static JwtProperties jwtProperties;

	public JwtUtils(JwtProperties jwtProperties) {
		JwtUtils.jwtProperties = jwtProperties;
	}

	public static boolean verify(String token) {
		try {
			String username = getClaim(token, AuthConstants.CLAIM_USERNAME);
			Algorithm algorithm = Algorithm.HMAC256(username + jwtProperties.getSecret());
			JWTVerifier verifier = JWT.require(algorithm).build();
			verifier.verify(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 生成token
	 *
	 * @param username
	 * @return
	 */
	public static String generateToken(String username) {
		long now = System.currentTimeMillis();
		Algorithm algo = Algorithm.HMAC256(username + jwtProperties.getSecret());
		return JWT.create()
				.withIssuer("zschina")
				.withIssuedAt(new Date(now))
				.withExpiresAt(new Date(now + jwtProperties.getTokenExpireTime() * 60 * 1000))
				.withClaim(AuthConstants.CLAIM_USERNAME, username)
				.sign(algo);
	}

	public static String getClaim(String token, String claim) {
		return JWT.decode(token).getClaim(claim).asString();
	}

}
