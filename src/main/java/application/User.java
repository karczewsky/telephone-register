package application;

import org.hibernate.Session;
import org.hibernate.annotations.NaturalId;
import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="USER_ACCOUNT")
public class User extends Persistable {
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


    @OneToMany(
            mappedBy = "owner",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<Phone> phones = new ArrayList<>();

    @Override
    public String toString() {
        String acc_type = this.is_admin ? "administrator" : "uzytkownik";
        return String.format("Id: %d\n%s: %s %s\nlogin: %s", this.id, acc_type, this.last_name,
                this.first_name, this.login);
    }

    String getNameFormated() {
        return String.format("%s \"%s\" %s", this.first_name, this.login, this.last_name);
    }

    static private void printTable() {
        System.out.printf("|%6s|%40s|%25s|%10s|\n", "ID", "Osoba", "Login", "Admin");
        Session s = Main.sessionFactory.openSession();
        List<User> users = s.createQuery("from User", User.class).getResultList();
        s.close();
        users.forEach(x->System.out.printf("|%6s|%40s|%25s|%10s|\n",
                x.id, x.last_name + " " + x.first_name, x.login, x.is_admin));
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



    static User createNewUser(boolean make_admin) {
        User u = new User();
        u.first_name = Utils.readString("Podaj imie: ", "Bledne imie!");
        u.last_name = Utils.readString("Podaj nazwisko: ", "Bledne nazwisko!");
        u.login = Utils.readString("Podaj login: ", "Bledny login");
        u.setNewPassword();
        u.is_admin = make_admin;

        return u;
    }

    static private User createNewUser() {
        return User.createNewUser(false);
    }

    static private User createNewAdministator() {
        return User.createNewUser(true);
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
                throw new NotAuthorizedException();
            }

            s.close();
            return user;
        } catch (NullPointerException e) {
            throw new NotAuthorizedException();
        }
    }

    static private User getUserByLogin(String login) throws NoSuchUserException {
        Session s = Main.sessionFactory.openSession();
        User u = s.bySimpleNaturalId(User.class).load(login);
        s.close();
        if (u == null)
            throw new NoSuchUserException();
        return u;
    }

    void menu() {
        System.out.println("Witaj w systemie Orwell: " + this.first_name + " " + last_name);
        boolean menuLoop = true;

        if(!this.is_admin) {
            while (menuLoop) {
                System.out.println("==== MENU UZYTKOWNIKA ====");
                System.out.println("0. Wyjdz z systemu");
                System.out.println("1. Wypisz numery");
                int choice = Utils.readPositiveInteger();

                switch (choice) {
                    default:
                        System.out.println("Nieznana opcja.");
                        break;
                    case 0:
                        menuLoop = false;
                        break;
                    case 1: {
                        System.out.println("Telefony nalezace do uzytkownika: ");
                        this.phones.forEach(x->System.out.printf("%s\n", x.getNumber()));
                        break;
                    }
                }
            }
        } else {
            while (menuLoop) {
                System.out.println("==== MENU ADMINISTRATORA ====");
                System.out.println("0. Wyjdz z systemu");
                System.out.println("1. Dodaj uzytkownika");
                System.out.println("2. Dodaj administratora");
                System.out.println("3. Wypisz wszystkie konta");
                System.out.println("4. Usun uzytkownika");
                System.out.println("5. Wypisz wszystkie telefony");
                System.out.println("6. Dodaj nowy numer telefonu");

                System.out.println("Co chcesz zrobic: ");
                int choice = Utils.readPositiveInteger();

                switch (choice) {
                    default:
                        System.out.println("Nieznana opcja.");
                        break;
                    case 0:
                        menuLoop = false;
                        break;
                    case 1: {
                        try {
                            User u = User.createNewUser();
                            u.save();
                            System.out.println(u);
                        } catch (PersistenceException e) {
                            System.out.println("Uzytkownik o podanym loginie juz istnieje");
                        }
                        break;
                    }
                    case 2: {
                        try {
                            User u = User.createNewAdministator();
                            u.save();
                            System.out.println(u);
                        } catch (PersistenceException e) {
                            System.out.println("Uzytkownik o podanym loginie juz istnieje");
                        }
                        break;
                    }
                    case 3: {
                        User.printTable();
                        break;
                    }
                    case 4: {
                        User.printTable();
                        System.out.println("Podaj login uzytkownika do usuniecia: ");
                        String login = Main.userInput.nextLine().trim();
                        try {
                            User u = User.getUserByLogin(login);
                            u.delete();
                        } catch (NoSuchUserException e) {
                            System.out.println("Podany uzytownik nie istnieje w bazie");
                        }

                        break;
                    }
                    case 5: {
                        Phone.printTable();
                        break;
                    }
                    case 6: {
                        User.printTable();
                        System.out.println("Podaj login uzytkownika, do ktorego ma byc przypisany telefon: ");
                        String login = Main.userInput.nextLine().trim();
                        try {
                            User u = User.getUserByLogin(login);
                            System.out.println("Podaj numer telefonu:");
                            long num = Utils.readPositiveLong();
                            if (num == -1) {
                                System.out.println("Niepoprawnie podany numer");
                            } else {
                                Phone phone = Phone.createNewPhone(num, u);
                                phone.save();
                            }
                        } catch (NoSuchUserException e) {
                            System.out.println("Podany uzytkownik nie istnieje w bazie");
                        } catch (PersistenceException e) {
                            System.out.println("Podany numer telefonu jest juz w bazie");
                        }

                        break;
                    }
                }
            }
        }

        System.out.println("Wychodzenie z systemu.");
    }




    static class NotAuthorizedException extends Exception {
        NotAuthorizedException() { super("Blad autoryzacji"); }
    }

    static class NoSuchUserException extends Exception {
        NoSuchUserException() { super("Podany uzytkownik nie istnieje"); }
    }
}



