package ru.practicum.server.user;

import lombok.NoArgsConstructor;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;

@NoArgsConstructor
public class UserMapper {

    public static UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();

        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());

        return userDto;
    }

    public static User toUser(UserCreateDto userCreateDto) {
        User user = new User();

        user.setName(userCreateDto.getName());
        user.setEmail(userCreateDto.getEmail());

        return user;
    }

    public static User updateUser(User user, UserCreateDto userCreateDto) {
        if (userCreateDto.getName() != null && !userCreateDto.getName().isEmpty()) {
            user.setName(userCreateDto.getName());
        }

        if (userCreateDto.getEmail() != null) {
            user.setEmail(userCreateDto.getEmail());
        }

        return user;
    }
}
