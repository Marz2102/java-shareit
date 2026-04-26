package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public boolean existsByEmail(String email) {
        return emails.contains(email);
    }

    @Override
    public User addUser(User user) {
        user.setId(generateNextId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());

        return user;
    }

    @Override
    public User updateUser(User user) {
        String oldEmail = users.get(user.getId()).getEmail();
        emails.remove(oldEmail);

        users.put(user.getId(), user);
        emails.add(user.getEmail());

        return user;
    }

    @Override
    public void deleteUser(Long id) {
        String oldEmail = users.get(id).getEmail();
        emails.remove(oldEmail);

        users.remove(id);
    }

    private Long generateNextId() {
        return users
                .keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0L) + 1;
    }
}
