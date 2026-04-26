package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserDto.UserCreateDto;
import ru.practicum.shareit.user.UserDto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(
            @Validated(UserCreateDto.OnCreate.class) @Valid @RequestBody UserCreateDto user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.addUser(user));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<UserDto> updateUser(
            @Valid @RequestBody UserCreateDto user,
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
