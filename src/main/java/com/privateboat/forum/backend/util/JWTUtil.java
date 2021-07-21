package com.privateboat.forum.backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.privateboat.forum.backend.entity.UserAuth;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTUtil {
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Authentication {
        public AuthenticationType type() default AuthenticationType.PASS;
    }

    public enum AuthenticationType {
        PASS, ADMIN, USER
    }

    private static final String SECRET = "comment_overflow";

    public static Map<String, Claim> getClaims(String token) throws JWTVerificationException {
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
        DecodedJWT jwt;
        jwt = jwtVerifier.verify(token);
        return jwt.getClaims();
    }

    public static String getToken(UserAuth userAuth) {
        Map<String, Object> map = new HashMap<>();
        map.put("alg", "HS256");
        map.put("typ", "JWT");

        Date now = new Date();
        // Default expire time is 1 month.
        Date expire = DateUtils.addMonths(new Date(), 1);

        return JWT.create()
                .withHeader(map)
                .withClaim("userId", userAuth.getUserId())
                .withClaim("userName", userAuth.getEmail())
                .withClaim("password", userAuth.getPassword())
                .withClaim("status", userAuth.getUserType().toString())
                .withIssuedAt(now)
                .withExpiresAt(expire)
                .sign(Algorithm.HMAC256(SECRET));
    }
}
