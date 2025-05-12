package com.longlz.taskboard.security.jwt;


import com.longlz.taskboard.security.service.UserDetailsImpl;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import java.util.Date;


@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${simpleLoginRegister.app.jwtSecret}")
    private String jwtSecret;

    @Value("${simpleLoginRegister.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    // generate JWT token
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token){
        return Jwts.parserBuilder().build().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken){
        try{
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch(MalformedJwtException e){
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch(ExpiredJwtException e){
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch(UnsupportedJwtException e){
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch(IllegalArgumentException e){
            logger.error("JWT claims String is empty: {}", e.getMessage());
        }

        return  false;
    }
}

