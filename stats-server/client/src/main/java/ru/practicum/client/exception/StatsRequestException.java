package ru.practicum.client.exception;

public class StatsRequestException extends RuntimeException {
    public StatsRequestException(String message) {
        super(message);
    }
}
