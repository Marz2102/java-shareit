package ru.practicum.server.user.storage;

import ru.practicum.server.user.model.User;

import java.util.Optional;

public interface UserStorage {

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Long id);
}
