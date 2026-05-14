package ru.practicum.server.user.service;

import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;

public interface UserService {

    UserDto getUserDtoById(Long id);

    User getUserById(Long id);

    boolean existsById(Long id);

    UserDto addUser(UserCreateDto userCreateDto);

    UserDto updateUser(Long id, UserCreateDto userCreateDto);

    void deleteUser(Long id);
}
