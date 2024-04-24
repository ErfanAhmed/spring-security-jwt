package net.therap.authService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author erfan
 * @since 9/20/23
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ErrorResponseDto implements Serializable {

    private String status;

    private String message;

    private String stacktrace; //if enabled

    private List<String> globalErrors;

    private Map<String, String> fieldErrors;

    public ErrorResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
        this.stacktrace = null;
        this.globalErrors =  null;
        this.fieldErrors = null;
    }
}
