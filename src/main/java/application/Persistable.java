package application;

import org.hibernate.Session;

abstract class Persistable {
    void save() {
        Session session = Main.sessionFactory.openSession();
        session.beginTransaction();
        session.save(this);
        session.getTransaction().commit();
        session.close();
    }

    void delete() {
        Session session = Main.sessionFactory.openSession();
        session.beginTransaction();
        session.delete(this);
        session.getTransaction().commit();
        session.close();
    }
}
