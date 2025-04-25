package com.sdp.menuservice.security;



import com.sdp.menuservice.config.JwtConfig;
import com.sdp.menuservice.exception.UnauthorizedException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;


import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;
    private String secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Base64.getEncoder().encodeToString(jwtConfig.getSecret().getBytes());
    }

    public String createToken(String username, List<String> roles) {
        Claims claims = (Claims) Jwts.claims().setSubject(username);
        claims.put("roles", roles);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUserDetailsFromToken(token);
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());
    }

    private UserDetails getUserDetailsFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        String username = claims.getSubject();
        List<String> roles = (List<String>) claims.get("roles");

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(username, "", authorities);
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid JWT token");
        }
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedException("Invalid or expired JWT token");
        }
    }


    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
}
