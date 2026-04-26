package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.UserDto.UserCreateDto;
import ru.practicum.shareit.user.UserDto.UserDto;

public interface UserService {

    UserDto getUserById(Long id);

    UserDto addUser(UserCreateDto userCreateDto);

    UserDto updateUser(Long id, UserCreateDto userCreateDto);

    void deleteUser(Long id);
}
