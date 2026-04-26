package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDto.UserCreateDto;
import ru.practicum.shareit.user.UserDto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

@Service
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserServiceImpl(final UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        return UserMapper.toUserDto(userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден")));
    }

    @Override
    public User getUserById(Long id) {
        return userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден"));
    }

    @Override
    public UserDto addUser(UserCreateDto userCreateDto) {
        if (userStorage.existsByEmail(userCreateDto.getEmail())) {
            throw new DuplicateException("Пользователь с почтой  - " + userCreateDto.getEmail() + " существует");
        }

        return UserMapper.toUserDto(userStorage.addUser(UserMapper.toUser(userCreateDto)));
    }

    @Override
    public UserDto updateUser(Long id, UserCreateDto userCreateDto) {
        if (userStorage.existsByEmail(userCreateDto.getEmail())) {
            throw new DuplicateException("Пользователь с почтой  - " + userCreateDto.getEmail() + " существует");
        }

        User updatedUser = userStorage.findById(id)
                .map(user -> UserMapper.updateUser(user, userCreateDto))
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден"));

        return UserMapper.toUserDto(userStorage.updateUser(updatedUser));

    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id);
        userStorage.deleteUser(id);
    }
}
