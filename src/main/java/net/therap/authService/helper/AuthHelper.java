package net.therap.authService.helper;

import lombok.RequiredArgsConstructor;
import net.therap.authService.dto.JwtTokenResponseDto;
import net.therap.authService.util.JwtTokenUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author erfan
 * @since 7/9/23
 */
@Component
@RequiredArgsConstructor
public class AuthHelper {

    private final JwtTokenUtils tokenUtils;
    private final AuthenticationManager authenticationManager;

    public JwtTokenResponseDto getAuthResponseDto(UserDetails userDetails) {
        String token = tokenUtils.getToken(userDetails);
        Date expiryDate = tokenUtils.getExpirationDateFromToken(token);

        return new JwtTokenResponseDto(token, expiryDate);
    }

    public void authenticate(String username, String password) throws DisabledException, BadCredentialsException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new DisabledException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        }
    }
}
