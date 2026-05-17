package ru.practicum.gateway.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.server.exception.exceptions.DuplicateException;
import ru.practicum.server.exception.exceptions.NotFoundException;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserServiceImpl;
import ru.practicum.server.user.storage.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testSaveUser() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("name");
        userCreateDto.setEmail("1@yandex.ru");

        when(userRepository.save(any(User.class))).thenAnswer(answer -> {
            User user = answer.getArgument(0);
            user.setId(1L);
            return user;
        });

        UserDto userDto = userService.addUser(userCreateDto);

        assertThat(userDto.getId()).isEqualTo(1);
        assertThat(userDto.getName()).isEqualTo("name");
        assertThat(userDto.getEmail()).isEqualTo("1@yandex.ru");
        verify(userRepository, times(1)).findByEmail("1@yandex.ru");
    }

    @Test
    void testSaveUserWithSameEmail() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("name");
        userCreateDto.setEmail("1@yandex.ru");

        when(userRepository.findByEmail("1@yandex.ru")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.addUser(userCreateDto))
                .isInstanceOf(DuplicateException.class);
        verify(userRepository, times(1)).findByEmail("1@yandex.ru");
    }

    @Test
    void testUpdateUser() {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName("NewName");
        userCreateDto.setEmail("1@yandex.ru");

        User oldUser = new User();
        oldUser.setId(1L);
        oldUser.setName("name");
        oldUser.setEmail("1@yandex.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(any(User.class))).thenReturn(oldUser);

        UserDto userDto = userService.updateUser(1L, userCreateDto);

        assertThat(userDto.getId()).isEqualTo(1);
        assertThat(userDto.getName()).isEqualTo("NewName");
        assertThat(userDto.getEmail()).isEqualTo("1@yandex.ru");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUserWhenUserNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(100L, new UserCreateDto()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testDeleteUser() {
        User user = new User();
        user.setId(1L);
        user.setName("name");
        user.setEmail("1@yandex.ru");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testDeleteUserWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(1L))
            .isInstanceOf(NotFoundException.class);
    }
}
