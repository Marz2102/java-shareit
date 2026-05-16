package ru.practicum.gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.user.dto.UserCreateDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void testGetUserById() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 1L, "name", "John"));

        when(userClient.getUserDtoById(eq(1L))).thenReturn(response);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void testGetUserByIdWhenUserNotFound() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(userClient.getUserDtoById(eq(2L))).thenReturn(response);

        mockMvc.perform(get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("John Smith", "1@yandex.ru");

        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 1L, "name", "John Smith",
                "email", "1@yandex.ru"));

        when(userClient.addUser(any(UserCreateDto.class))).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Smith"))
                .andExpect(jsonPath("$.email").value("1@yandex.ru"));
    }

    @Test
    void testCreateUserWithInvalidEmail() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("John Smith", "1yandex.ru");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("John Smith", "1@yandex.ru");
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 1, "name", "Johnny Smith"));

        when(userClient.updateUser(eq(1L), any(UserCreateDto.class))).thenReturn(response);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Johnny Smith"));
    }

    @Test
    void testUpdateUserWithBadEmail() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto("John Smith", "1yandex.ru");

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.noContent().build();

        when(userClient.deleteUser(eq(1L))).thenReturn(response);

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUserWhenUserNotFound() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(userClient.deleteUser(eq(2L))).thenReturn(response);

        mockMvc.perform(delete("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}