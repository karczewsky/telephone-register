package application;

import org.hibernate.Session;
import sun.awt.geom.AreaOp;

import javax.persistence.*;
import java.util.List;

@Entity
public class Phone extends Persistable {
    @Id
    private long number;

    @ManyToOne()
    @JoinColumn(name = "fk_user")
    private User owner;

    long getNumber() {
        return this.number;
    }

    static void printTable() {
        System.out.printf("|%12s|%40s|\n", "Numer", "Wlasciciel");
        Session s = Main.sessionFactory.openSession();
        List<Phone> phones = s.createQuery("from Phone", Phone.class).getResultList();
        s.close();
        phones.forEach(x ->
                System.out.printf("|%12s|%40s|\n", x.number, x.owner.getNameFormated())
        );
    }

    static private Phone createNewPhone(long number, User user) {
        Phone phone = new Phone();
        phone.owner = user;
        phone.number = number;

        return phone;
    }

    static void createNewPhoneFromUser() {
        User.printTable();
        User u = null;

        boolean err;
        do {
            System.out.println("Podaj login uzytkownika, do ktorego ma byc przypisany telefon: ");
            String login = Main.userInput.nextLine().trim();

            try {
                u = User.getUserByLogin(login);
                err = false;
            } catch (User.NoSuchUserException e) {
                err = true;
                System.out.println("Podany uzytkownik nie istnieje w bazie");
                continue;
            }

            if (u.getAdminStatus()) {
                err = true;
                System.out.println("Administrator nie moze posiadac telefonu");
            }
        } while (err);

        long num;
        do {
            System.out.println("Podaj numer telefonu:");
            num = Utils.readPositiveLong();
            if (num == -1) {
                System.out.println("Niepoprawnie podany numer");
            }
        } while (num == -1);

        Phone phone = Phone.createNewPhone(num, u);
        try {
            phone.save();
        } catch (PersistenceException e) {
            System.out.println("Podany telefon juz istnieje w bazie danych");
        }
    }
}