package com.clicksolutions.taskmanagement.serviceImpl;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.clicksolutions.taskmanagement.entity.User;
import com.clicksolutions.taskmanagement.exception.UserNotFound;
import com.clicksolutions.taskmanagement.repository.UserRepository;
import com.clicksolutions.taskmanagement.service.JWTService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JWTServiceImpl implements JWTService {

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User not found for email: {}", username);
            return new UserNotFound("User not found");
        });

        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("userId", user.getUserId())
                .claim("username", user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000)) // 7 days
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("Token generated successfully for user: {}", userDetails.getUsername());
        return token;
    }

    @Override
    public String extractUsername(String token) {
        log.debug("Extracting username from token");
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.debug("Extracting claim from token");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Key getSignKey() {
        log.debug("Retrieving signing key");
        try {
            log.debug("Using secret key: {}", secretKey);
            return Keys.hmacShaKeyFor(secretKey.getBytes());
        } catch (Exception e) {
            log.error("Error creating signing key: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve signing key");
        }
    }

    private Claims extractAllClaims(String token) {
        log.debug("Extracting all claims from token");
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating token for user: {}", userDetails.getUsername());
        final String username = extractUsername(token);
        boolean isValid = (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        if (isValid) {
            log.info("Token is valid for user: {}", userDetails.getUsername());
        } else {
            log.warn("Token is invalid or expired for user: {}", userDetails.getUsername());
        }
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        log.debug("Checking if token is expired");
        boolean isExpired = extractClaim(token, Claims::getExpiration).before(new Date());
        if (isExpired) {
            log.warn("Token has expired");
        }
        return isExpired;
    }
}
