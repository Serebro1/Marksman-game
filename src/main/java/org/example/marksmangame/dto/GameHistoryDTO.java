package org.example.marksmangame.dto;

import java.util.List;

public record GameHistoryDTO(
        List<GameHistoryEntryDTO> games
) implements MessageDTO {}
