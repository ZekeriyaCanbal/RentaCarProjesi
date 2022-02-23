package com.lecture.carrental.security.jwt;

import com.lecture.carrental.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTUtils {         // Bu class token oluşturmak ve log ları ile ilgili

    private static final Logger logger = LoggerFactory.getLogger(JWTUtils.class);   //Log tutmak için tanımlanır.

    @Value("${backendapi.app.jwtSecret}")
    private String jwtSecret;

    @Value("${backendapi.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    // Token oluşturduğumuz method
    public String genetateJwtToken(Authentication authentication){

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(""+(userDetails.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime()+jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

//  id üzerinden token de ki user bilgilere ulaşabileceğimiz method
    public Long getIdFromJwtToken (String token){
        return Long.parseLong(Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject());
    }

// Logger (Hataların Kaydı) tutulması için method
    public boolean validateJwtToken (String authToken){
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        catch (SecurityException e){
            logger.error("Invalid JWT signature (JWT imzası geçersiz) : {}", e.getMessage());
        }
        catch (MalformedJwtException e){
            logger.error("Invalid JWT token (Geçersiz JWT Token) : {}", e.getMessage());
        }
        catch (ExpiredJwtException e){
            logger.error("JWT token is expired (JWT token süresi doldu) : {}", e.getMessage());
        }
        catch (UnsupportedJwtException e){
            logger.error("JWT token is unsupported (JWT token desteklenmiyor) : {}", e.getMessage());
        }
        catch (IllegalArgumentException e){
            logger.error("JWT claims string is empty (JWT içeriği boş olduğu) : {}", e.getMessage());
        }
        return false;
    }


}
