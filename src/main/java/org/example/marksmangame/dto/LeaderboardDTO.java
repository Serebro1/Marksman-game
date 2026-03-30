package org.example.marksmangame.dto;

import java.io.Serial;
import java.util.List;

public record LeaderboardDTO(
        List<LeaderboardEntryDTO> entries
) implements MessageDTO {
    @Serial
    private static final long serialVersionUID = 1L;
}
