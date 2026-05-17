package ru.practicum.gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.item.dto.CommentCreateDto;
import ru.practicum.gateway.item.dto.ItemCreateDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    public ResponseEntity<Object> getCommentsForUserItems(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> addItem(ItemCreateDto item, Long userId) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(Long id, ItemCreateDto item, Long userId) {
        return patch("/{id}", userId, Map.of("id", id), item);
    }

    public ResponseEntity<Object> searchItems(String text, Long userId) {
        return get("/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(CommentCreateDto comment, Long userId, Long id) {
        return post("/{id}/comment", userId, Map.of("id", id), comment);
    }

    public ResponseEntity<Object> getCommentsByItemId(Long id, Long userId) {
        return get("/{id}", userId, Map.of("id", id));
    }
}
