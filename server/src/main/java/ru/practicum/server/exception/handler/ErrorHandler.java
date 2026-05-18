package ru.practicum.server.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.server.exception.exceptions.*;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, NotAvailableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final Exception e) {
        if (e instanceof NotAvailableException) {
            return new ErrorResponse("Данный ресурс недоступен в данный момент", e.getMessage());
        } else {
            return new ErrorResponse("Ошибка валидации данных", e.getMessage());
        }
    }

    @ExceptionHandler(DuplicateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateException(final DuplicateException e) {
        return new ErrorResponse("Ошибка уникальности данных", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleDataAccessException(final DataAccessException e) {
        return new ErrorResponse("Ошибка доступа к данным", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        return new ErrorResponse("Ресурс не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ErrorResponse handleUnsupportedMediaTypeException(final HttpMediaTypeNotSupportedException e) {
        return new ErrorResponse("Неподдерживаемый тип тела запроса", e.toString());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerException(final Exception e) {
        return new ErrorResponse("Ошибка сервера", e.getMessage());
    }
}
