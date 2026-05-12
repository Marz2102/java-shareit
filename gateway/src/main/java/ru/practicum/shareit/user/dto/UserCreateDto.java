package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserCreateDto {

    public interface Default {}

    public interface OnCreate extends ru.practicum.shareit.user.dto.UserCreateDto.Default {}

    private String name;

    @Email(groups = ru.practicum.shareit.user.dto.UserCreateDto.Default.class, message = "Введите почту в корректном формате")
    @NotNull(groups = ru.practicum.shareit.user.dto.UserCreateDto.OnCreate.class, message = "Укажите почту")
    private String email;
}
