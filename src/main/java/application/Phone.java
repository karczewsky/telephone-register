package application;

import org.hibernate.Session;

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

    static Phone createNewPhone(long number, User user) {
        Phone phone = new Phone();
        phone.owner = user;
        phone.number = number;

        return phone;
    }
}
