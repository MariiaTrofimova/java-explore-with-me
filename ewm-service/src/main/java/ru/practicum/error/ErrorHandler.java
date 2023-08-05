package ru.practicum.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.practicum.client.exception.StatsRequestException;
import ru.practicum.error.model.ErrorResponse;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleStatsRequestException(final StatsRequestException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(final IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException e) {
        ErrorResponse response = new ErrorResponse(String.format("Переменная %s: %s должна быть %s.",
                e.getName(), e.getValue(), Objects.requireNonNull(e.getRequiredType()).getSimpleName()));
        log.error("Переменная {}: {} должна быть {}.",
                e.getName(), e.getValue(), e.getRequiredType().getSimpleName());
        return response;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValid(final MethodArgumentNotValidException e) throws JsonProcessingException {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        log.error(mapper.writeValueAsString(errors), e);
        return new ErrorResponse(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.error(e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoHandlerFoundException(final NoHandlerFoundException e, WebRequest request) {
        log.error("Неизвестный запрос.");
        return new ErrorResponse("Неизвестный запрос.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Произошла непредвиденная ошибка: " + e.getMessage(), e);
        return new ErrorResponse("Произошла непредвиденная ошибка: " + e.getMessage());
    }
}