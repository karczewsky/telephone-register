package application;

import java.time.Duration;

class Utils {
    static String readString(String stdPrompt, String errPrompt) {
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

    static int readPositiveInteger() {
        int num;
        try {
            num = Integer.parseInt(Main.userInput.nextLine());
        } catch (NumberFormatException e) {
            num = -1;
        }

        if(num < 0)
            num = -1;
        return num;
    }

    static long readPositiveLong() {
        long num;
        try {
            num = Long.parseLong(Main.userInput.nextLine());
        } catch (NumberFormatException e) {
            num = -1;
        }

        if (num < 0)
            num = -1;
        return num;
    }

    static String prettyDuration(Duration duration) {
        long secs = duration.getSeconds();
        String out = "";

        if (secs >= 3600)
            out += String.format("%s godzin ", (long)(secs/3600));

        if (secs >= 60)
            out += String.format("%s minut ", (long)((secs%3600)/60));

        out += String.format("%s sekund", secs%60);

        return out;
    }
}
