package org.example.marksmangame.dto;

import java.io.Serial;


public record CommandDTO(
        CommandType type,
        String playerName
) implements MessageDTO {
    @Serial
    private static final long serialVersionUID = 1L;
}
