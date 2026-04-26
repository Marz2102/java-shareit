package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemCreateDto {

    public interface Default {}

    public interface OnCreate extends ItemCreateDto.Default {}

    @NotEmpty(groups = {OnCreate.class}, message = "Введите непустое название предмета")
    @NotNull(groups = {OnCreate.class}, message = "Укажите название предмета")
    private String name;

    @NotEmpty(groups = {OnCreate.class}, message = "Введите непустое описание предмета")
    @NotNull(groups = {OnCreate.class}, message = "Укажите описание предмета")
    private String description;

    @NotNull(groups = {OnCreate.class}, message = "Укажите доступность предмета")
    private Boolean available;
}
