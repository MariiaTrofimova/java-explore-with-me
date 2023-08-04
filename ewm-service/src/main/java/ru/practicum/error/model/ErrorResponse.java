package ru.practicum.error.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class ErrorResponse {
    String error;

    Map<String, String> validationErrors;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public String getError() {
        return error;
    }
}
