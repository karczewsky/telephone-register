package application;

import org.apache.log4j.BasicConfigurator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.Query;
import java.math.BigInteger;
import java.util.Scanner;


public class Main {
    static SessionFactory sessionFactory;
    static Scanner userInput;

    private static void init() {
        BasicConfigurator.configure();
        sessionFactory = new Configuration()
                .configure()
                .buildSessionFactory();
        Main.userInput = new Scanner(System.in);
    }

    public static void main(String[] args) {
        init();
        // Check if admin already exists in system
        // If not create new admin
        Session session = sessionFactory.openSession();
        Query query = session.createNativeQuery("select count(*) from user_account where is_admin=true");
        if((query.getSingleResult()).equals(BigInteger.ZERO)) {
            System.out.println("System nie posiada adminstatora, tworzenie administratora...");
            User a = User.createNewUser(true);
            session.beginTransaction();
            session.save(a);
            session.getTransaction().commit();
        }
        session.close();

        User user = null;
        try {
            user = User.userLogin();
        } catch (User.NotAuthorizedException e) {
            System.out.println("Bledne dane logowanie, zamykanie programu");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Blad krytyczny");
        }

        if(user == null)
            return;

        user.menu();

    }
}
