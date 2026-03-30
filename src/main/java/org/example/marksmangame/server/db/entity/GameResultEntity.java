package org.example.marksmangame.server.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "game_results")
public class GameResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String playerName;

    private int score;
    private int shots;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameEntity game;

    public GameResultEntity() {}
    public GameResultEntity(String playerName, int score, int shots, GameEntity game) {
        this.playerName = playerName;
        this.score = score;
        this.shots = shots;
        this.game = game;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public int getShots() { return shots; }
    public GameEntity getGame() { return game; }
    public void setGame(GameEntity game) { this.game = game; }
}
