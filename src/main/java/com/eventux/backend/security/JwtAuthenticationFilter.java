package com.eventux.backend.security;

import com.eventux.backend.repository.UserRepository;
import com.eventux.backend.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        System.out.println(">>> Checking shouldNotFilter for path: " + path);
        return path.equals("/api/auth/signin") || path.equals("/api/auth/signup");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println(">>> JwtAuthenticationFilter HIT for: " + path);

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println(">>> No Authorization header or wrong format");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(jwt);

        System.out.println(">>> Extracted userEmail: " + userEmail);
        System.out.println(">>> JwtAuthenticationFilter HIT for: " + request.getRequestURI());
        System.out.println(">>> Authorization Header: " + request.getHeader("Authorization"));
        System.out.println(">>> Extracted userEmail from token: " + userEmail);
        System.out.println(">>> Setting Authentication for: " + userEmail);
        System.out.println(">>> Current SecurityContext: " + SecurityContextHolder.getContext().getAuthentication());

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null && jwtService.isTokenValid(jwt, userEmail)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userEmail,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER")) // ✅ FIXED HERE
                        );


                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println(">>> Authentication set for: " + userEmail);
            } else {
                System.out.println(">>> Invalid token or user not found");
            }
        } else {
            System.out.println(">>> Skipping auth setup");
        }

        filterChain.doFilter(request, response);
    }
}
