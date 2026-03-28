package org.example.marksmangame.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record GameHistoryDTO(
        List<GameHistoryEntryDTO> games
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
