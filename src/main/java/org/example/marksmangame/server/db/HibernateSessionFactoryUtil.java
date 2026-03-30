package org.example.marksmangame.server.db;

import org.example.marksmangame.server.db.entity.GameEntity;
import org.example.marksmangame.server.db.entity.GameResultEntity;
import org.example.marksmangame.server.db.entity.PlayerStatsEntity;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
public final class HibernateSessionFactoryUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateSessionFactoryUtil() {}

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();
            configuration.addAnnotatedClass(PlayerStatsEntity.class);
            configuration.addAnnotatedClass(GameResultEntity.class);
            configuration.addAnnotatedClass(GameEntity.class);
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            return configuration.buildSessionFactory(registry);
        } catch (Exception e) {
            System.err.println("Ошибка при создании SessionFactory: " + e.getMessage());
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null) {
            SESSION_FACTORY.close();
        }
    }
}
