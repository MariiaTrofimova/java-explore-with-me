package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.practicum.model.ApiError;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    private static final LocalDateTime NOW = LocalDateTime.now();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Required request parameter is not present")
                .message(e.getMessage())
                .timestamp(NOW)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.error(e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(NOW)
                .build();
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNoHandlerFoundException(final NoHandlerFoundException e, WebRequest request) {
        log.error("Неизвестный запрос: {}", request.getHeaderNames());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(NOW)
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable e) {
        log.error("Произошла непредвиденная ошибка: " + e.getMessage(), e);
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .reason("An unexpected error has occurred.")
                .message(e.getMessage())
                .timestamp(NOW)
                .build();
    }
}