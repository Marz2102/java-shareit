package ru.practicum.gateway.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.server.ShareItServer;
import ru.practicum.server.user.dto.UserCreateDto;
import ru.practicum.server.user.dto.UserDto;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserDtoJsonTest {
    private final JacksonTester<UserDto> jsonUserDto;
    private final JacksonTester<UserCreateDto> jsonUserCreateDto;

    @Test
    void testSerializeUserDto() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("name");
        userDto.setEmail("email");

        JsonContent<UserDto> jsonContent = jsonUserDto.write(userDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("email");
    }

    @Test
    void testDeserializeUserCreateDto() throws Exception {
        String json = "{\"name\":\"name\",\"email\":\"email\"}";

        UserCreateDto userCreateDto = jsonUserCreateDto.parse(json).getObject();

        assertThat(userCreateDto.getName()).isEqualTo("name");
        assertThat(userCreateDto.getEmail()).isEqualTo("email");
    }
}
