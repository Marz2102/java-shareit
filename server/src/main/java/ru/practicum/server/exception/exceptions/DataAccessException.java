package ru.practicum.server.exception.exceptions;

public class DataAccessException extends RuntimeException {

    public DataAccessException(String message) {
        super(message);
    }
}
