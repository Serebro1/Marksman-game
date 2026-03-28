package org.example.marksmangame.db.dao;

import org.example.marksmangame.db.HibernateSessionFactoryUtil;
import org.example.marksmangame.db.entity.GameEntity;
import org.example.marksmangame.db.entity.GameResultEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class GameDAO {
    public GameEntity createGame(String name) {
        GameEntity game = new GameEntity(name);
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(game);
            tx.commit();
            return game;
        }
    }

    public void saveGameResults(GameEntity game, List<GameResultEntity> results, String winner) {
        game.finish(winner);
        game.setResults(results);
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.merge(game);
            for (GameResultEntity result : results) {
                session.persist(result);
            }
            tx.commit();
        }
    }

    public List<GameEntity> getAllGames() {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery("from GameEntity g order by g.id desc", GameEntity.class)
                    .getResultList();
        }
    }

    public List<GameResultEntity> getResultsForGame(GameEntity game) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "from GameResultEntity r where r.game.id = :gameId order by r.score desc",
                            GameResultEntity.class)
                    .setParameter("gameId", game.getId())
                    .getResultList();
        }
    }
}
