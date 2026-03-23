package org.example.marksmangame.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
public final class HibernateSessionFactoryUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateSessionFactoryUtil() {}

    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration()
                    .configure()
                    .buildSessionFactory();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError("SessionFactory init failed: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }
}
