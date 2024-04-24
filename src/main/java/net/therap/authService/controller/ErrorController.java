package net.therap.authService.controller;

import lombok.extern.slf4j.Slf4j;
import net.therap.authService.dto.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static java.util.Objects.nonNull;
import static net.therap.authService.util.ApiResponseUtil.errorResponse;
import static net.therap.authService.util.ApiResponseUtil.validationErrorResponseDto;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * @author erfan
 * @since 9/20/23
 */
@Slf4j
@ControllerAdvice
public class ErrorController extends ResponseEntityExceptionHandler {

    @Value("${app.error.response.printStackTrace: false}")
    private boolean printStackTrace = false;

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             HttpHeaders headers,
                                                             HttpStatusCode statusCode,
                                                             WebRequest request) {
        log.info("[ERROR]", ex);

        if (ex instanceof MethodArgumentNotValidException) {
            ErrorResponseDto errorResponseDto = validationErrorResponseDto(((MethodArgumentNotValidException) ex).getBindingResult());

            return createResponseEntity(
                    errorResponseDto,
                    headers,
                    statusCode,
                    request
            );
        }

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                HttpStatus.valueOf(statusCode.value()).toString(),
                ex.getMessage(),
                printStackTrace ? getStackTrace(ex) : null,
                null,
                null
        );

        return createResponseEntity(
                errorResponseDto,
                headers,
                statusCode,
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnhandledExceptions(Exception ex) {
        log.info("[ERROR]", ex);

        ResponseStatus responseStatus = AnnotationUtils.getAnnotation(ex.getClass(), ResponseStatus.class);
        HttpStatus httpStatus = nonNull(responseStatus) ? responseStatus.value() : BAD_REQUEST;

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                httpStatus.toString(),
                ex.getMessage(),
                printStackTrace ? getStackTrace(ex) : null,
                null,
                null
        );

        return errorResponse(
                errorResponseDto,
                httpStatus
        );
    }
}
