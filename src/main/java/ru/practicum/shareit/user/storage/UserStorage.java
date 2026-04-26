package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserStorage {

    Optional<User> findById(Long id);

    boolean existsByEmail(String email);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);
}
