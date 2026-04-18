package org.example.marksmangame.dto;

public record LeaderboardEntryDTO (
        String username,
        int wins
) implements MessageDTO {}
