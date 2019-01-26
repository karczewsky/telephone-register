package application;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Event extends Persistable {
    @Id
    private long id;

    @ManyToOne()
    @JoinColumn(name = "fk_start")
    private Phone from_phone;

    @ManyToOne()
    @JoinColumn(name = "fk_to")
    private Phone to_phone;

    private Date start;
    private Date finish;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        ANSWERED,
        UNANSWERED;

        Status readFromUser() {
            boolean err = false;
            while (true) {
                if (err)
                    System.out.println("Nieznana opcja podana.");
                System.out.println("Czy polaczenie odebrane[tak/nie]:");

                String line = Main.userInput.nextLine().toLowerCase().trim();

                if (line.equals("tak") || line.equals("t"))
                    return ANSWERED;

                if (line.equals("nie") || line.equals("n"))
                    return UNANSWERED;

                err = true;
            }
        }
    }
}
