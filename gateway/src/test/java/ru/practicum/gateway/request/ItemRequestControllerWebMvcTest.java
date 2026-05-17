package ru.practicum.gateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.request.dto.CreateRequestDto;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetRequestById() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 1L, "description", "Some usual description"));

        when(itemRequestClient.getRequestById(eq(1L), eq(1L))).thenReturn(response);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Some usual description"));
    }

    @Test
    void testGetRequestByIdWhenRequestNotFound() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(itemRequestClient.getRequestById(eq(2L), eq(1L))).thenReturn(response);

        mockMvc.perform(get("/requests/2")
                        .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateRequest() throws Exception {
        CreateRequestDto createRequestDto = new CreateRequestDto("Some request");
        ResponseEntity<Object> response = ResponseEntity.status(201).body(
                (Map.of("id", 1L, "description", "Some usual description")));

        when(itemRequestClient.createRequest(any(CreateRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Some usual description"));
    }

    @Test
    void testCreateRequestsWhenItemNotFound() throws Exception {
        CreateRequestDto createRequestDto = new CreateRequestDto("description");
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(itemRequestClient.createRequest(any(CreateRequestDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(createRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRequestsWhenUserNotInHeader() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        when(itemRequestClient.getRequests(any(Long.class))).thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}