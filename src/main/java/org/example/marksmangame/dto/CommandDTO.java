package org.example.marksmangame.dto;

import java.io.Serial;
import java.io.Serializable;


public record CommandDTO(
        CommandType type,
        String playerName
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
