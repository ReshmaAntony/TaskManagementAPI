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
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTServiceImpl implements JWTService{
	
	@Autowired
	private UserRepository userRepository;
	
	 @Value("${jwt.secret}") 
	 private String secretKey;
	
    public String generateToken(UserDetails userDetails) {
	   String email = userDetails.getUsername();
	   User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFound("usernotfound"));
		return Jwts.builder().setSubject(userDetails.getUsername())
				.claim("userId", user.getUserId())
				.claim("username", user.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() +604800000))
				.signWith(getSignkey(), SignatureAlgorithm.HS256)
				.compact();
	}

	@Override
	public String extractUsername(String token) {
		
		return extractClaim(token, Claims::getSubject);
	}
	
	private <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
		final Claims claims = extractAallClaim(token);
		return claimsResolver.apply(claims);
		
	}
	 private Key getSignkey() {
		 System.out.println();
	        byte[] key = Decoders.BASE64.decode(secretKey);
	        System.out.println("secretKey" +secretKey );
	        System.out.println("key" +key );
	        return Keys.hmacShaKeyFor(key);
		}
	
	private Claims extractAallClaim(String token) {
		return Jwts.parserBuilder().setSigningKey(getSignkey()).build().parseClaimsJws(token).getBody();
	}

	@Override
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	private boolean isTokenExpired(String token) {
		return extractClaim(token, Claims::getExpiration).before(new Date());
	}

}