package org.example.marksmangame.db.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String gameName;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private String winner;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameRoundEntity> rounds = new ArrayList<>();

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameResultEntity> results = new ArrayList<>();

    public GameEntity() {};

    public GameEntity(String gameName) {
        this.gameName = gameName;
        this.startedAt = LocalDateTime.now();
    }

    public void finish(String winner) {
        this.winner = winner;
        this.finishedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }
    public LocalDateTime getStartedAt() { return startedAt; }
    public LocalDateTime getFinishedAt() { return finishedAt; }
    public String getWinner() { return winner; }

    public void setRounds(List<GameRoundEntity> rounds) { this.rounds = rounds; }
    public void setResults(List<GameResultEntity> results) { this.results = results; }
    public void addResult(GameResultEntity result) {
        if (results != null) {
            results.add(result);
        }
    }
}
