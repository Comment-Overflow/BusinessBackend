package com.privateboat.forum.backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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
}
