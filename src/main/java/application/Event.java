package application;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Entity
public class Event extends Persistable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne()
    @JoinColumn(name = "fk_start")
    private Phone from_phone;

    @ManyToOne()
    @JoinColumn(name = "fk_to")
    private Phone to_phone;

    private LocalDateTime start_ts;
    private LocalDateTime end_ts;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Override
    public String toString() {
        return String.format("Status: %s Start: %s End: %s From: %s To: %s",
                this.status, this.start_ts, this.end_ts, this.from_phone, this.to_phone);
    }

    public enum Status {
        ANSWERED,
        UNANSWERED;

        @Override
        public String toString() {
            if (this == ANSWERED)
                return "Odebrane";
            else
                return "Nieodebrane";
        }

        static Status readFromUser() {
            boolean err = false;
            while (true) {
                if (err)
                    System.out.println("Bledna opcja.");
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

    static void createNewEvenFromUser() {
        // k - hour in day (1-24)
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("dd-MM-yyyy kk:mm:ss");

        System.out.println("Sposob wpisywania daty/czasu: " + LocalDateTime.now().format(formatDate));

        Event event = new Event();
        boolean err = true;
        do {
            try {
                System.out.println("Podaj czas rozpoczecia polaczenia:");
                String line = Main.userInput.nextLine().trim();
                event.start_ts = LocalDateTime.parse(line, formatDate);
                err = false;
            } catch (DateTimeParseException e) {
                System.out.println("Bledny format");
            }
        } while (err);


        event.status = Status.readFromUser();
        if (event.status == Status.ANSWERED) {
            do {
                err = true;
                try {
                    System.out.println("Podaj czas zakoczenia polaczenia:");
                    String line = Main.userInput.nextLine().trim();
                    event.end_ts = LocalDateTime.parse(line, formatDate);
                    Duration duration = Duration.between(event.start_ts, event.end_ts);

                    if (duration.isNegative() || duration.isZero()) {
                        System.out.println("Polaczenie musi trawac min. 1 sekunde");
                    } else {
                        err = false;
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Bledny format");
                }
            } while (err);
        } else {
            event.end_ts = null;
        }

        err = false;
        Phone.printTable();
        do {
            System.out.println("Podaj numer telefonu z ktorego wykonano polaczenie:");
            long nr = Utils.readPositiveInteger();
            if (nr == -1) {
                System.out.println("Bledny numer");
            } else {
                try {
                    err = false;
                    event.from_phone = Phone.getByNumber(nr);
                } catch (Phone.NoSuchPhone e) {
                    err = true;
                    System.out.println("Brak podanego telefonu w bazie danych");
                }
            }
        } while (err);

        do {
            System.out.println("Podaj numer telefonu do ktorego wykonano polaczenie:");
            long nr = Utils.readPositiveInteger();
            if (nr == -1) {
                System.out.println("Bledny numer");
            } else {
                try {
                    err = false;
                    event.to_phone = Phone.getByNumber(nr);
                } catch (Phone.NoSuchPhone e) {
                    err = true;
                    System.out.println("Brak podanego telefonu w bazie danych");
                }
            }
        } while (err);

        event.save();
        System.out.println(event);
    }
}
