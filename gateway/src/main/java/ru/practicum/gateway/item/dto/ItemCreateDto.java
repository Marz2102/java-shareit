package ru.practicum.gateway.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemCreateDto {

    public interface Default {}

    public interface OnCreate extends Default {}

    @NotBlank(groups = {OnCreate.class}, message = "Введите непустое название предмета")
    private String name;

    @NotBlank(groups = {OnCreate.class}, message = "Введите непустое описание предмета")
    private String description;

    @NotNull(groups = {OnCreate.class}, message = "Укажите доступность предмета")
    private Boolean available;

    @Positive(message = "id запроса должен быть положительным")
    private Long requestId;
}