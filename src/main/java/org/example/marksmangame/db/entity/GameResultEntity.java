package org.example.marksmangame.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_results")
public class GameResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playerName;
    private int score;
    private int shots;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity game;

    public GameResultEntity() {}
    public GameResultEntity(String player, int score, int shots, GameEntity game) {
        this.playerName = playerName;
        this.score = score;
        this.shots = shots;
        this.game = game;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getShots() {
        return shots;
    }

    public void setShots(int shots) {
        this.shots = shots;
    }

    public GameEntity getGame() {
        return game;
    }

    public void setGame(GameEntity game) {
        this.game = game;
    }
}
