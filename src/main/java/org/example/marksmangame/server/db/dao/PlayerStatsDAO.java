package org.example.marksmangame.server.db.dao;

import org.example.marksmangame.server.db.HibernateSessionFactoryUtil;
import org.example.marksmangame.server.db.entity.PlayerStatsEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class PlayerStatsDAO {
    public void ensureExists(String username) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            PlayerStatsEntity entity = session
                    .createSelectionQuery("from PlayerStatsEntity where username = :u", PlayerStatsEntity.class)
                    .setParameter("u", username)
                    .uniqueResult();
            if (entity == null) {
                Transaction tx = session.beginTransaction();
                session.persist(new PlayerStatsEntity(username));
                tx.commit();
            }
        }
    }

    public void addWin(String username) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            PlayerStatsEntity entity = session
                    .createSelectionQuery("from PlayerStatsEntity where username = :u", PlayerStatsEntity.class)
                    .setParameter("u", username)
                    .uniqueResult();

            if (entity == null) {
                entity = new PlayerStatsEntity(username);
                entity.addWin();
                session.persist(entity);
            } else {
                entity.addWin();
                session.merge(entity);
            }

            tx.commit();
        }
    }

    public List<PlayerStatsEntity> topWins(int limit) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                            "from PlayerStatsEntity order by wins desc, username asc",
                            PlayerStatsEntity.class
                    )
                    .setMaxResults(limit)
                    .list();
        }
    }
}
