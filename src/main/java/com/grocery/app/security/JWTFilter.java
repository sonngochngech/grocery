package com.grocery.app.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Service
public class JWTFilter extends OncePerRequestFilter {
    @Autowired
    private JWTUtil JWTUtil;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorHeader=request.getHeader("Authorization");
        if(authorHeader!=null && authorHeader.startsWith("Bearer ")) {
            String token=authorHeader.substring(7);
            try{
                if(token.isBlank()) {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token is invalid");
                    return;
                }
                String username=JWTUtil.getUsername(token);
                UserDetails userDetails=userDetailsService.loadUserByUsername(username);
                boolean isValid=JWTUtil.isTokenValid(token, userDetails);
                if (isValid){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null);
                    if(SecurityContextHolder.getContext().getAuthentication()==null) {
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                }
            }catch(JWTVerificationException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token is invalid");
                return;

            }
        }
        filterChain.doFilter(request, response);
    }


    
}
