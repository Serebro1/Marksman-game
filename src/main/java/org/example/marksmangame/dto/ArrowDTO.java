package org.example.marksmangame.dto;

import java.io.Serializable;

public record ArrowDTO(
        double x,
        double y,
        int playerId,
        boolean active
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
