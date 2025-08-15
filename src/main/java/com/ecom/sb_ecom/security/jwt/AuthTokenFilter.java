package com.ecom.sb_ecom.security.jwt;

import com.ecom.sb_ecom.exceptions.ApiException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            // fetch token from header
            //String token = jwtUtils.getJwtTokenFromHeader(request);

            // fetch token from cookie
            String token = jwtUtils.getTokenFromCookie(request);

            if (token == null) {
                throw new ApiException("Token is not provided, Please provide token");
            }

            // verify the token
            jwtUtils.validateJwtToken(token);

            // extract userName from Token
            String userName = jwtUtils.getUserNameFromJwtToken(token);
            UserDetails user = userDetailsService.loadUserByUsername(userName);

            // prepare auth object
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            // set context
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }catch (ApiException e){
            // continue
        }

        filterChain.doFilter(request, response);
    }
}
