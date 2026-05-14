package ru.practicum.server.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.server.item.dto.ItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullItemRequestDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemResponseDto> items;
}

