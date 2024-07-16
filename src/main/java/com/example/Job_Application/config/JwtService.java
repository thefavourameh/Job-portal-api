package com.example.Job_Application.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
@Service
public class JwtService {
    @Value("${app.jwt-secret}")
    private String SECRET_KEY;

    @Value("${app.jwt-expiration}")
    private Long jwtExpirationDate;

    public String generateToken(UserDetails user){
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("authorities", populateAuthorities(user.getAuthorities()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationDate))
                .signWith(getSigningKey())
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities){
        Set<String> authoritiesSet = new HashSet<>();
        for (GrantedAuthority authority: authorities){
            authoritiesSet.add(authority.getAuthority());
        }
        return String.join(",", authoritiesSet);
    }

    public String getUserName(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();


    }
    //this method help us validate that the token belongs to the right person
    public boolean validateToken(String token){
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parse(token);

            return true;
        } catch (ExpiredJwtException | IllegalArgumentException | SecurityException | MalformedJwtException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractUsernameFromToken(String token) {

        String jwtToken = token.replace("Bearer ", "");

        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(jwtToken)
                .getBody()
                .getSubject();
    }
}
