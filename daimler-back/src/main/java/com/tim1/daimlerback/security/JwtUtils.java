package com.tim1.daimlerback.security;

import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.tim1.daimlerback.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

    @Value("${jwtSecret}")
    private String jwtSecret;

    @Value("${jwtExpirationMs}")
    private long jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date((new Date()).getTime() + jwtExpirationMs))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return JWT.decode(token).getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(authToken);
            return true;
        } catch(JWTDecodeException e) {
            return false;
        }
    }
}
