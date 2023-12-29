package com.fa.backend.filters;


import com.fa.backend.config.JwtCore;
import com.fa.backend.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtCore jwtCore;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String headerAuth = request.getHeader("Authorization");

            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                String jwt = headerAuth.substring(7);
                String username = jwtCore.getUserNameFromJwt(jwt);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            // Отправка ошибки клиенту
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Expired JWT token");
        } catch (UsernameNotFoundException e) {
            logger.error("Authentication error: {}", e);

            // Отправка ошибки клиенту
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("You cannot transfer an authorization token during registration");
        } catch (Exception e) {
            // Логирование других ошибок
            logger.error("Authentication error: {}", e);

            // Отправка ошибки клиенту
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write("Internal Server Error");
        }

    }
}
