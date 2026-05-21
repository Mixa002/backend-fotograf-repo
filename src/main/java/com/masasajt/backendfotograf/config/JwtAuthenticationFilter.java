package com.masasajt.backendfotograf.config;

import com.masasajt.backendfotograf.service.JwtService;
import com.masasajt.backendfotograf.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Proširena provera: Ako nema headera, ako ne počinje sa Bearer,
        // ili ako je frontend greškom poslao tekst "Bearer null" ili "Bearer undefined"
        if (authHeader == null || !authHeader.startsWith("Bearer ") ||
                authHeader.equals("Bearer null") || authHeader.equals("Bearer undefined")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Izvlacimo cist token
        jwt = authHeader.substring(7);

        // 2. Ceo proces parsiranja tokena stavljamo u try-catch
        try {
            // Pomoću tvog JwtService-a izvlačimo username iz tokena
            username = jwtService.extractClaim(jwt, io.jsonwebtoken.Claims::getSubject);

            // Ako imamo username, a korisnik jos uvek nije autentifikovan u tekucem Spring kontekstu
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Proveravamo da li je token validan
                if (jwtService.isTokenValid(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Ubacujemo korisnika u SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Ako je token nevalidan, izmenjen ili prazan, hvatamo grešku ovde.
            // Ne rušimo aplikaciju, već samo puštamo anonimnog korisnika (Guest) dalje.
            System.out.println("Anonimni posetilac ili nevalidan token: " + e.getMessage());
        }

        // Prosledi zahtev sledecem filteru u lancu (bilo da je token prošao ili je uhvaćen u catch-u)
        filterChain.doFilter(request, response);
    }
}