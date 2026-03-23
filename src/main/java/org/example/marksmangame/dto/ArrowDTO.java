package org.example.marksmangame.dto;

import java.io.Serial;
import java.io.Serializable;

public record ArrowDTO(
        double x,
        double y,
        int playerId,
        boolean active
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
