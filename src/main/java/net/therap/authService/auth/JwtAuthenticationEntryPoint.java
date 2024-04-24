package net.therap.authService.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.therap.authService.dto.ErrorResponseDto;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * @author erfan
 * @since 7/6/23
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = 1L;

    private final MessageSourceAccessor msa;

    public JwtAuthenticationEntryPoint(MessageSourceAccessor msa) {
        this.msa = msa;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("auth exception", authException);

        ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.UNAUTHORIZED.toString(),
                msa.getMessage("auth.error",
                        new String[]{authException.getClass().getSimpleName(), authException.getMessage()}));

        objectWriter.writeValue(response.getWriter(), errorResponseDto);
    }
}
