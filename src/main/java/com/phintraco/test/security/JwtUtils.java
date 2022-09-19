package com.phintraco.test.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JwtUtils {
    @Value("${test.app.jwtSecret}")
    private String jwtSecret;
    @Value("${test.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl usePrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder().setSubject((usePrincipal.getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException exception) {
            log.error("Invalid JWT Signature: {}", exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.error("Invalid JWT Token : {}", exception.getMessage());
        } catch (ExpiredJwtException exception) {
            log.error("Jwt Token Expired : {}", exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.error("Jwt Token is Unsupported : {}", exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.error("Jwt Claim string is empty : {}", exception.getMessage());
        }
        return false;
    }
}
