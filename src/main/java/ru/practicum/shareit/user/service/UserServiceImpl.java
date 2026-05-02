package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserDto.UserCreateDto;
import ru.practicum.shareit.user.UserDto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        return UserMapper.toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден")));
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден"));
    }

    @Override
    public UserDto addUser(UserCreateDto userCreateDto) {
        if (userRepository.findByEmail(userCreateDto.getEmail()).isPresent()) {
            throw new DuplicateException("Пользователь с почтой  - " + userCreateDto.getEmail() + " существует");
        }

        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userCreateDto)));
    }

    @Override
    public UserDto updateUser(Long id, UserCreateDto userCreateDto) {
        if (userCreateDto.getEmail() != null) {
            Optional<User> optionalUser = userRepository.findByEmail(userCreateDto.getEmail());
            if (optionalUser.isPresent() && !optionalUser.get().getId().equals(id)) {
                throw new DuplicateException("Пользователь с почтой  - " + userCreateDto.getEmail() + " существует");
            }
        }

        User updatedUser = userRepository.findById(id)
                .map(user -> UserMapper.updateUser(user, userCreateDto))
                .orElseThrow(() -> new NotFoundException("Пользователь с id - " + id + " не найден"));

        return UserMapper.toUserDto(userRepository.save(updatedUser));
    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id);
        userRepository.deleteById(id);
    }
}
