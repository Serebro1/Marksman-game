package org.example.marksmangame.dto;

import java.io.Serial;
import java.io.Serializable;

public record TargetDTO(
        int id,
        double x,
        double y,
        double radius,
        boolean active
) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
