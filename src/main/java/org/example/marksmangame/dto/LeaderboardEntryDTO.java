package org.example.marksmangame.dto;

import java.io.Serial;
import java.io.Serializable;

public record LeaderboardEntryDTO (
        String username,
        int wins
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
