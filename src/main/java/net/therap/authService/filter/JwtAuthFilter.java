package net.therap.authService.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import net.therap.authService.domain.User;
import net.therap.authService.service.AuthService;
import net.therap.authService.util.JwtTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.security.sasl.AuthenticationException;
import java.io.IOException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author erfan
 * @since 7/6/23
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtTokenUtils jwtTokenUtils;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        log.info("- doFilterInternal for uri : " + request.getRequestURI());

        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;

        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (nonNull(requestTokenHeader) && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                username = jwtTokenUtils.getUsernameFromToken(jwtToken);

            } catch (IllegalArgumentException e) {
                log.warn("Unable to get JWT Token");

            } catch (ExpiredJwtException e) {
                log.warn("JWT Token has expired for uri : " + request.getRequestURI());

            } catch (Exception e) {
                log.warn("Invalid JWT Token for uri : " + request.getRequestURI());
            }

            log.info("Found valid username form the token");
        } else {
            log.warn("JWT Token does not begin with Bearer String");
        }

        // Once we get the token validate it.
        if (isNotBlank(username) && isNull(SecurityContextHolder.getContext().getAuthentication())) {
            User user = authService.findByUsername(username);

            if (jwtTokenUtils.validateToken(jwtToken, user)) {

                UsernamePasswordAuthenticationToken token =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(token);

                log.info("Token is validated successfully!");
            } else {
                log.warn("Invalid token!");
                throw new AuthenticationException("Invalid token exception!");
            }
        }

        filterChain.doFilter(request, response);
    }
}
