package application;

import org.hibernate.Session;
import org.hibernate.annotations.NaturalId;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;


@Entity
@Table(name="USER_ACCOUNT")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String first_name;

    private String last_name;
    @NaturalId
    @Column(unique = true)
    private String login;
    private String pass_hash;
    private boolean is_admin;

    @Override
    public String toString() {
        return String.format("User<id=%d fname=%s lname=%s is_admin=%b>",
                this.id, this.first_name, this.last_name, this.is_admin);
    }

    private static boolean securityPolicy(String password) {
        boolean ok = password.length() >= 8;
        if (!ok) {
            System.out.println("Haslo nie spelnia polityki bezpieczenstwa");
            System.out.println("Haslo musi miec min. 8 znakow");
        }
        return ok;
    }

    private void setNewPassword() {

        String pass;
        do {
            System.out.println("Podaj nowe haslo: ");
            pass = Main.userInput.nextLine().trim();
        } while (!securityPolicy(pass));

        this.pass_hash = BCrypt.hashpw(pass, BCrypt.gensalt(12));
    }

    private static String readString(String stdPrompt, String errPrompt) {
        String s;
        boolean err = false;
        do {
            if (err)
                System.out.println(errPrompt);
            System.out.println(stdPrompt);
            s = Main.userInput.nextLine().trim();
            err = s.length() < 3;
        } while (err);
        return s;
    }

    static User createNewUser(boolean make_admin) {
        User u = new User();
        u.first_name = User.readString("Podaj imie: ", "Bledne imie!");
        u.last_name = User.readString("Podaj nazwisko: ", "Bledne nazwisko!");
        u.login = User.readString("Podaj login: ", "Bledny login");
        u.setNewPassword();
        u.is_admin = make_admin;

        return u;
    }

    static User createNewUser() {
        return User.createNewUser(false);
    }

    static User userLogin() throws Exception {
        try {
            System.out.println("Logowanie do systemu Orwell");
            System.out.println("Podaj login: ");
            String login = Main.userInput.nextLine().trim();
            System.out.println("Podaj haslo: ");
            String pass = Main.userInput.nextLine().trim();
            Session s = Main.sessionFactory.openSession();
            User user = s.bySimpleNaturalId(User.class).load(login);
            if (BCrypt.checkpw(pass, user.pass_hash)) {
                System.out.println("Poprawne haslo");
            } else {
                throw new User.NotAuthorized();
            }

            return user;
        } catch (NullPointerException e) {
            throw new User.NotAuthorized();
        }
    }

    void menu() {
        System.out.println("Witaj w systemie Orwell: " + this.first_name + " " + last_name);
        boolean menuLoop = true;
        while (menuLoop) {
            System.out.println("==== MENU UZYTKOWNIKA ====");
            System.out.println("0. Wyjdz z systemu");
            System.out.println("Co chcesz zrobic: ");

            int choice;
            try {
                choice = Integer.parseInt(Main.userInput.nextLine());
            } catch (NumberFormatException e) {
                choice = -1;
            }

            switch (choice) {
                default:
                    System.out.println("Nieznana opcja.");
                    break;
                case 0:
                    menuLoop = false;
                    break;
            }
        }
        System.out.println("Wychodzenie z systemu.");
    }


    static class NotAuthorized extends Exception {
        NotAuthorized() {
            super("Blad autoryzacji");
        }
    }
}



