package org.example.marksmangame.dto;

import java.io.Serial;
import java.io.Serializable;

public record PlayerDTO(
        int id,
        String name,
        int score,
        int shots,
        boolean ready
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
