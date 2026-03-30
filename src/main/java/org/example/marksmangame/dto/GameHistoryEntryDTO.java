package org.example.marksmangame.dto;

import java.io.Serial;
import java.time.LocalDateTime;

public record GameHistoryEntryDTO(
        String gameName,
        String winner,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) implements MessageDTO {
    @Serial
    private static final long serialVersionUID = 1L;
}
