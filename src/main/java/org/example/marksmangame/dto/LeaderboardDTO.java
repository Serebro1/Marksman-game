package org.example.marksmangame.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record LeaderboardDTO(
        List<LeaderboardEntryDTO> entries
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
