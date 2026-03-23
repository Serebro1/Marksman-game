package org.example.marksmangame.db;

import org.example.marksmangame.db.entity.PlayerStatsEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class StatsRepository {
    public void ensurePlayerExists(String username) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            PlayerStatsEntity entity = session
                    .createQuery("from PlayerStatsEntity where username = :u", PlayerStatsEntity.class)
                    .setParameter("u", username)
                    .uniqueResult();

            if (entity == null) {
                session.persist(new PlayerStatsEntity(username));
            }

            tx.commit();
        }
    }

    public void addWin(String username) {
        try (Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            PlayerStatsEntity entity = session
                    .createQuery("from PlayerStatsEntity where username = :u", PlayerStatsEntity.class)
                    .setParameter("u", username)
                    .uniqueResult();

            if (entity == null) {
                entity = new PlayerStatsEntity(username);
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
            return session.createQuery(
                            "from PlayerStatsEntity order by wins desc, username asc",
                            PlayerStatsEntity.class
                    )
                    .setMaxResults(limit)
                    .list();
        }
    }
}
