package application;

import org.apache.log4j.BasicConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import java.math.BigInteger;


public class Main {
    static SessionFactory sessionFactory;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();
        // Check if admin already exists in system
        Query query = session.createNativeQuery("SELECT count(*) from user_account");
        if(((BigInteger)query.getSingleResult()).compareTo(new BigInteger("0")) == 0) {
            System.out.println("System nie posiada adminstatora, tworzenie administratora...");
            User a = User.create_new_user(true);
            session.beginTransaction();
            session.save(a);
            session.getTransaction().commit();
        }

        session.close();
    }
}
