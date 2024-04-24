package net.therap.authService.controller;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.therap.authService.domain.User;
import net.therap.authService.dto.ErrorResponseDto;
import net.therap.authService.dto.LoginDto;
import net.therap.authService.dto.UserResponseDto;
import net.therap.authService.helper.AuthHelper;
import net.therap.authService.jwt.JwtToken;
import net.therap.authService.jwt.JwtTokenService;
import net.therap.authService.repository.UserRepository;
import net.therap.authService.util.JwtTokenUtils;
import org.modelmapper.ModelMapper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static net.therap.authService.util.ApiResponseUtil.errorResponse;
import static net.therap.authService.util.ApiResponseUtil.successResponse;

/**
 * @author erfan
 * @since 7/9/23
 */
@RestController
@Slf4j
@RequestMapping(value = "/api/v1/tokens")
@RequiredArgsConstructor
public class AuthController {

    private final MessageSourceAccessor msa;
    private final AuthHelper authHelper;
    private final JwtTokenUtils tokenUtils;
    private final UserRepository userRepository;
    private final JwtTokenService tokenService;
    private final ModelMapper modelMapper;

    @PostMapping()
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginDto loginDto) {
        try {
            authHelper.authenticate(loginDto.getUsername(), loginDto.getPassword());

        } catch (BadCredentialsException e) {
            ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                    HttpStatus.BAD_REQUEST.toString(),
                    msa.getMessage("invalid.credentials")
            );

            return errorResponse(errorResponseDto);
        }

        UserDetails userDetails = userRepository.findByUsername(loginDto.getUsername()).get();

        return successResponse(authHelper.getAuthResponseDto(userDetails));
    }

    @PostMapping(value = "/token-validation")
    public ResponseEntity<?> validateAuthToken() {
        log.info("AUTH-SERVICE :: validate");

        String token;

        try {
            token = tokenUtils.getToken();
        } catch (NullPointerException npe) {
            log.warn("No token found!", npe);

            return errorResponse(
                    new ErrorResponseDto(
                            HttpStatus.UNAUTHORIZED.toString(),
                            msa.getMessage("token.not.found")));
        } catch (Exception e) {
            log.warn("Something went wrong!", e);

            return errorResponse(
                    new ErrorResponseDto(HttpStatus.BAD_REQUEST.toString(),
                            msa.getMessage("error.something.went.wrong")));
        }

        /**
         * since token has already been validated by JwtAuthFilter class,
         * we don't need to validated it here again.
         */
        String username;
        try {
            username = tokenUtils.getUsernameFromToken(token);

        } catch (ExpiredJwtException expiredJwtException) {
            return errorResponse(
                    new ErrorResponseDto(
                            HttpStatus.UNAUTHORIZED.toString(),
                            msa.getMessage("error.token.expired")));
        }

        User optionalUser = userRepository.findByUsername(username).get();

        return successResponse(UserResponseDto.prepareUserResponse(optionalUser));
    }

    @PostMapping(value = "/token-invalidation")
    public ResponseEntity<?> logout() {
        String token = tokenUtils.getToken();
        String username = tokenUtils.getUsernameFromToken(token);

        tokenService.invalidate(
                username,
                tokenUtils.getExpirationDateFromToken(token).getTime());

        return successResponse(msa.getMessage("token.invalidate"));
    }
}
