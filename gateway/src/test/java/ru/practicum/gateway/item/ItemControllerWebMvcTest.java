package ru.practicum.gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.gateway.item.dto.CommentCreateDto;
import ru.practicum.gateway.item.dto.ItemCreateDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateItem() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto("item", "Some usual description", true, null);
        ResponseEntity<Object> response = ResponseEntity.status(201).body((Map.of("id", 1L, "name", "item",
                "description", "Some usual description", "available", true)));

        when(itemClient.addItem(any(ItemCreateDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("Some usual description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testCreateItemWithEmptyName() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto("", "desc", true, 1L);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateItem() throws Exception {
        ItemCreateDto itemCreateDto = new ItemCreateDto("", "desc", true, 1L);
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("description", "Some usual description",
                "available", false, "requestId", 1L));

        when(itemClient.updateItem(eq(1L), any(ItemCreateDto.class), eq(1L))).thenReturn(response);

        mockMvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void testSearchItems() throws Exception {
        when(itemClient.searchItems(eq("item"), eq(1L))).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items/search?text=item")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchItemsWithoutParameters() throws Exception {
        mockMvc.perform(get("/items/search"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddComment() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto("Good item!");
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 1L, "text", "Good item!",
                "authorName", "Some author", "created", LocalDate.of(2000, 1, 1).toString()));

        when(itemClient.addComment(any(CommentCreateDto.class), eq(1L), eq(1L))).thenReturn(response);

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Good item!"))
                .andExpect(jsonPath("$.authorName").value("Some author"))
                .andExpect(jsonPath("$.created").value(LocalDate.of(2000, 1, 1).toString()));
    }

    @Test
    void testAddCommentWithBlankText() throws Exception {
        CommentCreateDto commentCreateDto = new CommentCreateDto("      ");

        mockMvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto))
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCommentsByItemId() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.ok(Map.of("id", 1L, "name", "Some item",
                "description", "Some usual description", "available", true));

        when(itemClient.getCommentsByItemId(eq(1L), eq(1L))).thenReturn(response);

        mockMvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Some item"))
                .andExpect(jsonPath("$.description").value("Some usual description"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void testGetCommentsByItemIdWhenItemNotFound() throws Exception {
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        when(itemClient.getCommentsByItemId(eq(2L), eq(1L))).thenReturn(response);

        mockMvc.perform(get("/requests/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetItems() throws Exception {
        when(itemClient.getCommentsForUserItems(eq(1L))).thenReturn(ResponseEntity.ok(List.of()));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testGetItemsWithoutUserId() throws Exception {
        mockMvc.perform(get("/items"))
                .andExpect(status().isBadRequest());
    }
}
