package ru.practicum.gateway.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.item.dto.ItemResponseDto;
import ru.practicum.server.request.dto.CreateRequestDto;
import ru.practicum.server.request.dto.FullItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoJsonTest {
    private final JacksonTester<ItemRequestDto> jsonItemRequestDto;
    private final JacksonTester<FullItemRequestDto> jsonFullItemRequestDto;
    private final JacksonTester<CreateRequestDto> jsonCreateRequestDto;

    @Test
    void testSerializeItemRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(1L);
        itemRequestDto.setDescription("description");
        itemRequestDto.setCreated(created);

        JsonContent<ItemRequestDto> jsonContent = jsonItemRequestDto.write(itemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("description");
    }

    @Test
    void testSerializeFullItemRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.now().withNano(0);

        FullItemRequestDto fullItemRequestDto = new FullItemRequestDto();
        fullItemRequestDto.setId(1L);
        fullItemRequestDto.setDescription("description");
        fullItemRequestDto.setCreated(created);
        fullItemRequestDto.setItems(List.of(new ItemResponseDto(2L, "itemName", 3L)));

        JsonContent<FullItemRequestDto> content = jsonFullItemRequestDto.write(fullItemRequestDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(content).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(content).extractingJsonPathArrayValue("$.items").hasSize(1);
        assertThat(content).extractingJsonPathNumberValue("$.items[0].itemId").isEqualTo(2);
        assertThat(content).extractingJsonPathStringValue("$.items[0].name").isEqualTo("itemName");
        assertThat(content).extractingJsonPathNumberValue("$.items[0].ownerId").isEqualTo(3);
    }

    @Test
    void testDeserializeCreateRequestDto() throws Exception {
        String json = "{\"description\":\"description\"}";

        CreateRequestDto createRequestDto = jsonCreateRequestDto.parse(json).getObject();

        assertThat(createRequestDto.getDescription()).isEqualTo("description");
    }
}
