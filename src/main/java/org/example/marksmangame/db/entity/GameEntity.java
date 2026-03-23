package org.example.marksmangame.db.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "games")
public class GameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gameName;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    private String winner;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<GameResultEntity> results;

    public GameEntity() {};

    public GameEntity(String gameName) {
        this.gameName = gameName;
        this.startedAt = LocalDateTime.now();
    }

    public void finish(String winner) {
        this.winner = winner;
        this.finishedAt = LocalDateTime.now();
    }
}
