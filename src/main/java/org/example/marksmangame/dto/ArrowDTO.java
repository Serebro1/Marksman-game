package org.example.marksmangame.dto;

import java.io.Serial;

public record ArrowDTO(
        double x,
        double y,
        int playerId,
        boolean active
) implements MessageDTO {
    @Serial
    private static final long serialVersionUID = 1L;
}
