package com.xming.gatekeeper.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.xming.gatekeeper.api.AuthUser;
import io.javalin.http.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class JwtManager {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtManager(String secret) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).withIssuer("gatekeeper").build();
    }

    public String issueToken(String username, Set<String> roles) {
        return JWT.create()
                .withIssuer("gatekeeper")
                .withSubject(username)
                .withClaim("roles", new ArrayList<>(roles))
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + 3600_000)) // 1 小时
                .sign(algorithm);
    }

    public AuthUser verifyToken(String token) {
        DecodedJWT jwt = verifier.verify(token);
        String username = jwt.getSubject();
        Set<String> roles = Set.of(jwt.getClaim("roles").asArray(String.class));
        return new SimpleAuthUser(username, roles);
    }

    public AuthUser verifyTokenFromContext(Context ctx) {
        String authHeader = ctx.header("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring("Bearer ".length());
        return verifyToken(token);
    }
}
