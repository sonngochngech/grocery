package com.grocery.app.security;

import java.security.Key;
import java.util.Date;
import java.util.Map;


import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.Payload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;



@Component
public class JWTUtil {

    private final String secret="cl$ksc^nas%lkdm!cz@xclk#as&dmka;s28*392da%jskd#n9827@3498%";

    public String generateToken(
        String username,
        Long expirationTime
        ) throws  IllegalArgumentException, JWTCreationException {

        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis()+expirationTime))
                .sign(Algorithm.HMAC256(secret));
    }

    private DecodedJWT extractClaim(String token) throws JWTVerificationException {
        return JWT.require(Algorithm.HMAC256(secret)).build().verify(token);
    }

    public boolean isExpired(String token)  {
        return extractClaim(token).getExpiresAt().before(new Date());
    }

    public  boolean isTokenValid(String token,UserDetails userDetails) throws JWTVerificationException {
        return !isExpired(token) && extractClaim(token).getSubject().equals(userDetails.getUsername());
    }

    public String getUsername(String token) {
        return extractClaim(token).getSubject();
    }





}
