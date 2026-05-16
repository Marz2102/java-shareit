package ru.practicum.gateway.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemDtoJsonTest {
    private final JacksonTester<ItemDto> jsonItemDto;
    private final JacksonTester<ItemResponseDto> jsonItemResponseDto;
    private final JacksonTester<ItemCommentsDto> jsonItemCommentsDto;
    private final JacksonTester<CommentDto> jsonCommentDto;
    private final JacksonTester<ItemCreateDto> jsonItemCreateDto;
    private final JacksonTester<CommentCreateDto> jsonCommentCreateDto;

    @Test
    void testSerializeItemDto() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);

        JsonContent<ItemDto> jsonContent = jsonItemDto.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isTrue();
    }

    @Test
    void testSerializeItemResponseDto() throws Exception {
        ItemResponseDto itemResponseDto = new ItemResponseDto();
        itemResponseDto.setItemId(1L);
        itemResponseDto.setName("name");
        itemResponseDto.setOwnerId(1L);

        JsonContent<ItemResponseDto> jsonContent = jsonItemResponseDto.write(itemResponseDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
    }

    @Test
    void testSerializeItemCommentsDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().minusYears(1).withNano(0);
        LocalDateTime end = LocalDateTime.now().plusYears(1).withNano(0);

        ItemCommentsDto itemCommentsDto = new ItemCommentsDto();
        itemCommentsDto.setId(1L);
        itemCommentsDto.setName("name");
        itemCommentsDto.setDescription("description");
        itemCommentsDto.setAvailable(false);
        itemCommentsDto.setLastBooking(start);
        itemCommentsDto.setNextBooking(end);
        itemCommentsDto.setComments(List.of());

        JsonContent<ItemCommentsDto> jsonContent = jsonItemCommentsDto.write(itemCommentsDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isFalse();
        assertThat(jsonContent).extractingJsonPathStringValue("$.lastBooking").isEqualTo(start.toString());
        assertThat(jsonContent).extractingJsonPathStringValue("$.nextBooking").isEqualTo(end.toString());
        assertThat(jsonContent).extractingJsonPathArrayValue("$.comments").isEmpty();
    }

    @Test
    void testSerializeCommentDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().minusYears(1).withNano(0);

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("text");
        commentDto.setAuthorName("author");
        commentDto.setCreated(created);

        JsonContent<CommentDto> jsonContent = jsonCommentDto.write(commentDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo("author");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created").isEqualTo(created.toString());
    }

    @Test
    void testDeserializeItemCreateDto() throws Exception {
        String json = "{\"name\":\"name\", \"description\":\"description\", \"available\":true, \"requestId\":10}";

        ItemCreateDto itemCreateDto = jsonItemCreateDto.parse(json).getObject();

        assertThat(itemCreateDto.getName()).isEqualTo("name");
        assertThat(itemCreateDto.getDescription()).isEqualTo("description");
        assertThat(itemCreateDto.getAvailable()).isTrue();
        assertThat(itemCreateDto.getRequestId()).isEqualTo(10L);
    }

    @Test
    void testDeserializeCommentCreateDto() throws Exception {
        String json = "{\"text\":\"text\"}";

        CommentCreateDto commentCreateDto = jsonCommentCreateDto.parse(json).getObject();

        assertThat(commentCreateDto.getText()).isEqualTo("text");
    }
}
