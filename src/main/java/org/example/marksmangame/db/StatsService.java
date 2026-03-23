package org.example.marksmangame.db;

import org.example.marksmangame.dto.LeaderboardDTO;
import org.example.marksmangame.dto.LeaderboardEntryDTO;

public class StatsService {
    private final StatsRepository repository = new StatsRepository();

    public void registrePlayer(String username) {
        repository.ensurePlayerExists(username);
    }

    public void recordWin(String username) {
        repository.addWin(username);
    }

    public LeaderboardDTO getLeaderboard(int limit) {
        var rows = repository.topWins(limit).stream()
                .map(p -> new LeaderboardEntryDTO(p.getUsername(), p.getWins()))
                .toList();
        return new LeaderboardDTO(rows);
    }
}
