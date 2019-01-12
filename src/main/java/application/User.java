package application;

import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.util.Scanner;


@Entity
@Table(name="USER_ACCOUNT")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    private String first_name;

    private String last_name;
    @Column(unique = true)
    private String login;
    private String pass_hash;
    private boolean is_admin;

    @Override
    public String toString() {
        return String.format("User<id=%d fname=%s lname=%s is_admin=%b>",
                this.id, this.first_name, this.last_name, this.is_admin);
    }

    private static boolean security_policy(String password) {
        boolean ok = password.length() >= 8;
        if(!ok) {
            System.out.println("Haslo nie spelnia polityki bezpieczenstwa");
            System.out.println("Haslo musi miec min. 8 znakow");
        }
        return ok;
    }

    private void set_new_password() {
        Scanner sc = new Scanner(System.in);

        String pass;
        do {
            System.out.println("Podaj nowe haslo: ");
            pass = sc.nextLine();
        } while(!security_policy(pass));

        this.pass_hash = BCrypt.hashpw(pass, BCrypt.gensalt(12));
    }

    private static String read_string(String stdPrompt, String errPrompt) {
        String s;
        Scanner sc = new Scanner(System.in);
        boolean err = false;
        do {
            if (err)
                System.out.println(errPrompt);
            System.out.println(stdPrompt);
            s = sc.nextLine().trim();
            err = s.length() < 3;
        } while (err);
        return s;
    }



    static User create_new_user(boolean make_admin) {
        User u = new User();
        u.first_name = User.read_string("Podaj imie: ", "Bledne imie!");
        u.last_name = User.read_string("Podaj nazwisko: ", "Bledne nazwisko!");
        u.login = User.read_string("Podaj login: ", "Bledny login");
        u.set_new_password();
        u.is_admin = make_admin;

        return u;
    };



}
