package org.example.marksmangame.db;

import org.example.marksmangame.db.dao.PlayerStatsDAO;
import org.example.marksmangame.dto.LeaderboardDTO;
import org.example.marksmangame.dto.LeaderboardEntryDTO;

public class GameService {
    private final PlayerStatsDAO playerDao = new PlayerStatsDAO();

    public void registerPlayer(String username) {
        playerDao.ensureExists(username);
    }

    public void recordWin(String username) {
        playerDao.addWin(username);
    }

    public LeaderboardDTO getLeaderboard(int limit) {
        var rows = playerDao.topWins(limit).stream()
                .map(p -> new LeaderboardEntryDTO(p.getUsername(), p.getWins()))
                .toList();
        return new LeaderboardDTO(rows);
    }
}
