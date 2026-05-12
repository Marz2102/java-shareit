package ru.practicum.shareit.user;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    public UserController(final UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return userClient.getUserDtoById(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(
            @Validated(UserCreateDto.OnCreate.class) @RequestBody UserCreateDto user) {
        return userClient.addUser(user);
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<Object> updateUser(
            @Validated(UserCreateDto.Default.class) @RequestBody UserCreateDto user,
            @PathVariable Long id) {
        return userClient.updateUser(id, user);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        return userClient.deleteUser(id);
    }
}