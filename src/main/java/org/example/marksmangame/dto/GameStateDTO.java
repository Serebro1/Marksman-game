package org.example.marksmangame.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record GameStateDTO(
        List<TargetDTO> targets,
        List<ArrowDTO> arrows,
        List<PlayerDTO> players,
        GameState state,
        String pausedBy,
        String winnerName
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
