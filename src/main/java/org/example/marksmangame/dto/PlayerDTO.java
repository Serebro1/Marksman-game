package org.example.marksmangame.dto;

import java.io.Serial;

public record PlayerDTO(
        int id,
        String name,
        int score,
        int shots,
        boolean ready
) implements MessageDTO {
    @Serial
    private static final long serialVersionUID = 1L;
}
