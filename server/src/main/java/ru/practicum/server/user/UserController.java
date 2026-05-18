package ru.practicum.server.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserDtoById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserCreateDto user) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.addUser(user));
    }

    @PatchMapping(path = "/{id}")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UserCreateDto user,
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
