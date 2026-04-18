package org.example.marksmangame.dto;

import java.time.LocalDateTime;

public record GameHistoryEntryDTO(
        String gameName,
        String winner,
        LocalDateTime startedAt,
        LocalDateTime finishedAt
) implements MessageDTO {}
