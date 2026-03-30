package org.example.marksmangame.dto;

import java.io.Serial;

public record TargetDTO(
        int id,
        double x,
        double y,
        double radius,
        boolean active
) implements MessageDTO {
    @Serial
    private static final long serialVersionUID = 1L;
}
