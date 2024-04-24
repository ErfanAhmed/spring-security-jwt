package net.therap.authService.util;

import net.therap.authService.dto.ErrorResponseDto;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

/**
 * @author erfan
 * @since 9/20/23
 */
public class ApiResponseUtil {

    public static <T> ResponseEntity<T> successResponse(T responseBody) {

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    public static <T> ResponseEntity<T> successResponse(T responseBody, HttpStatus httpStatus) {

        assert httpStatus.is2xxSuccessful();

        return ResponseEntity
                .status(httpStatus)
                .body(responseBody);
    }

    public static <T> ResponseEntity<T> errorResponse(T responseBody) {

        return ResponseEntity
                .badRequest()
                .body(responseBody);
    }

    public static <T> ResponseEntity<T> errorResponse(T responseBody, HttpStatus statusCode) {

        assert statusCode.isError();

        return ResponseEntity
                .status(statusCode)
                .body(responseBody);
    }

    public static ResponseEntity<ErrorResponseDto> validationErrorResponse(BindingResult result) {

        return errorResponse(
                validationErrorResponseDto(result),
                UNPROCESSABLE_ENTITY
        );
    }

    public static ErrorResponseDto validationErrorResponseDto(BindingResult result) {

        return new ErrorResponseDto(
                UNPROCESSABLE_ENTITY.toString(),
                "Failed Validating Object",
                null,
                result.getGlobalErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .collect(Collectors.toList()),

                result.getFieldErrors()
                        .stream()
                        .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage))
        );
    }
}
