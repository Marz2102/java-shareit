package ru.practicum.gateway.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive(message = "id предмета должно быть положительным")
    private Long itemId;

    @NotNull(message = "Укажите начало бронирования")
    @FutureOrPresent(message = "Нельзя указать уже прошедшую дату")
    private LocalDateTime start;

    @NotNull(message = "Укажите окончание бронирования")
    @FutureOrPresent(message = "Нельзя указать уже прошедшую дату")
    private LocalDateTime end;

    @AssertTrue(message = "Время окончания бронирования не может быть раньше времени начала")
    public boolean isValid() {
        return end.isAfter(start);
    }
}