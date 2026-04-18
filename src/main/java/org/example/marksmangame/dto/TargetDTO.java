package org.example.marksmangame.dto;

public record TargetDTO(
        int id,
        double x,
        double y,
        double radius,
        boolean active
) implements MessageDTO {}
