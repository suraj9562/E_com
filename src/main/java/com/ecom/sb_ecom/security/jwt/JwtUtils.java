package com.ecom.sb_ecom.security.jwt;

import com.ecom.sb_ecom.exceptions.ApiException;
import com.ecom.sb_ecom.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${application.jwtExpirationInMS}")
    private long jwtExpirationInMS;

    @Value("${application.jwtSecret}")
    private String jwtSecret;

    @Value("${application.jwtCookieName}")
    private String jwtCookie;

    @Value("${application.maxCookieAge}")
    private Long cookieAge;

    // extract token from header
    public String getJwtTokenFromHeader(HttpServletRequest request){
        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer ")){
            return null;
        }

        return header.substring(7);
    }

    public String getTokenFromCookie(HttpServletRequest request){
        Cookie cookie = WebUtils.getCookie(request, jwtCookie);

        if(cookie == null){
            return null;
        }

        return cookie.getValue();
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl user){
        String token = generateJwtTokenUsingUserName(user);

        return ResponseCookie.from(jwtCookie, token)
                .path("/api")
                .maxAge(cookieAge)
                .httpOnly(false)
                .build();
    }

    // extract username from token
    public String getUserNameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // verify the token
    public void validateJwtToken(String token){
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(token);
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException e){
            throw new ApiException("Invalid JWT token: " + e.getMessage());
        }catch (IllegalArgumentException e) {
            throw new ApiException("JWT claims string is empty: " + e.getMessage());
        }
    }

    // generate token using userName
    public String generateJwtTokenUsingUserName(UserDetailsImpl userDetails){
        String username = userDetails.getUsername();

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationInMS))
                .signWith(key())
                .compact();
    }

    // key generation
    public SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public ResponseCookie signOut() {
        return ResponseCookie.from(jwtCookie, null).path("/api").build();
    }
}
