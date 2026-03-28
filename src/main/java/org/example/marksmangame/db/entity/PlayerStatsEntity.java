package org.example.marksmangame.db.entity;

import jakarta.persistence.*;

import java.io.Serializable;

@Entity
@Table(
        name = "player_stats",
        uniqueConstraints = @UniqueConstraint(columnNames = "username")
)
public class PlayerStatsEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String username;

    @Column(nullable = false)
    private int wins = 0;

    public PlayerStatsEntity() {}

    public PlayerStatsEntity(String username) {
        this.username = username;
    }

    public String getUsername() { return username; }
    public int getWins() { return wins; }

    public void addWin() { wins++; }
}
