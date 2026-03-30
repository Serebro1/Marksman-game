package org.example.marksmangame.dto;

import java.io.Serial;

public record LeaderboardEntryDTO (
        String username,
        int wins
) implements MessageDTO {
    @Serial
    private static final long serialVersionUID = 1L;
}
