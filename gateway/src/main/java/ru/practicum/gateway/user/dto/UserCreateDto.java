package ru.practicum.gateway.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

    public interface OnCreate extends Default {}

    @NotBlank(groups = OnCreate.class, message = "Укажите непустое имя")
    private String name;

    @Email(groups = Default.class, message = "Введите почту в корректном формате")
    @NotNull(groups = OnCreate.class, message = "Укажите почту")
    private String email;
}
