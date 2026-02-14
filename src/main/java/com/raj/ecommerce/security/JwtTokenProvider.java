package com.raj.ecommerce.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration-ms}")
    private long validityInMs;

//    @PostConstruct
//    protected  void init(){
//        secretKey= Base64.getEncoder().encodeToString(secretKey.getBytes());
//    }

    public Key getSignKey(){
        byte[] keyBytes= Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(String email, List<String> roles){
        Claims claims= Jwts.claims().setSubject(email);
        claims.put("roles",roles);
        Date now=new Date();
        Date validity=new Date(now.getTime()+validityInMs);
        return Jwts.builder()
                .setSubject(email)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSignKey())
                .compact();
    }

    public <T> T extractClaims(String token, Function<Claims,T> claimsResolver){
        final Claims claims=extractAllclaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllclaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token){
        return extractClaims(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token,Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails){
        final String username=extractEmail(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}
