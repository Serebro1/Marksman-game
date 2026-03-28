package org.example.marksmangame.db;

import org.example.marksmangame.db.dao.GameDAO;
import org.example.marksmangame.db.dao.PlayerStatsDAO;
import org.example.marksmangame.db.entity.GameEntity;
import org.example.marksmangame.db.entity.GameResultEntity;
import org.example.marksmangame.dto.GameHistoryDTO;
import org.example.marksmangame.dto.GameHistoryEntryDTO;
import org.example.marksmangame.dto.LeaderboardDTO;
import org.example.marksmangame.dto.LeaderboardEntryDTO;
import org.example.marksmangame.model.Player;
import org.example.marksmangame.server.Engine;

import java.util.ArrayList;
import java.util.List;

public class GameService {
    private final PlayerStatsDAO playerDao = new PlayerStatsDAO();
    private final GameDAO gameDAO = new GameDAO();
    private GameEntity currentGame;

    public void startGame(String name) {
        currentGame = gameDAO.createGame(name);
    }

    public void finishGame(Engine engine, String winner) {
        List<GameResultEntity> results = new ArrayList<>();

        for (Player p : engine.getPlayers()) {
            results.add(new GameResultEntity(
                    p.getName(),
                    p.getScore(),
                    p.getShots(),
                    currentGame
            ));
        }

        gameDAO.saveGameResults(currentGame, results, winner);
        currentGame = null;
    }

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

    public GameHistoryDTO getHistory(int limit) {
        List<GameHistoryEntryDTO> list = gameDAO.getAllGames().stream()
                .limit(limit)
                .map(g -> new GameHistoryEntryDTO(
                        g.getGameName(),
                        g.getWinner(),
                        g.getStartedAt(),
                        g.getFinishedAt()
                ))
                .toList();

        return new GameHistoryDTO(list);
    }
}
