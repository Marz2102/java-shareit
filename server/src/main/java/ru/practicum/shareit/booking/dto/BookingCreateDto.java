package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingCreateDto {

    @NotNull(message = "Укажите id предмета для бронирования")
    private Long itemId;

    @NotNull(message = "Укажите начало бронирования")
    @FutureOrPresent(message = "Нельзя указать уже прошедшую дату")
    private LocalDateTime start;

    @NotNull(message = "Укажите окончание бронирования")
    @FutureOrPresent(message = "Нельзя указать уже прошедшую дату")
    private LocalDateTime end;
}
