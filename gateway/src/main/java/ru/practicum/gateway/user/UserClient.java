package ru.practicum.gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.user.dto.UserCreateDto;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> getUserDtoById(Long userId) {
        return get("/{id}", userId, Map.of("id", userId));
    }

    public ResponseEntity<Object> addUser(UserCreateDto user) {
        return post("", user);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserCreateDto user) {
        return patch("/{id}", userId, Map.of("id", userId), user);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/{id}", userId, Map.of("id", userId));
    }
}
