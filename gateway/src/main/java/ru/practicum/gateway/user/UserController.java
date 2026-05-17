package ru.practicum.gateway.user;

import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.user.dto.UserCreateDto;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    public UserController(final UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Object> getUserById(@Positive @PathVariable Long id) {
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
            @Positive @PathVariable Long id) {
        return userClient.updateUser(id, user);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Object> deleteUser(@Positive @PathVariable Long id) {
        return userClient.deleteUser(id);
    }
}