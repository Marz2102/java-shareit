package ru.practicum.server.exception.exceptions;

public class ServerRequestException extends RuntimeException {

    public ServerRequestException(String message) {
        super(message);
    }
}
