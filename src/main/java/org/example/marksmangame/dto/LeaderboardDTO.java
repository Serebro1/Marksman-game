package org.example.marksmangame.dto;

import java.util.List;

public record LeaderboardDTO(
        List<LeaderboardEntryDTO> entries
) implements MessageDTO {}
