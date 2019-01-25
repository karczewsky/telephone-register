package application;

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
        return num;
    }

    static long readPositiveLong() {
        long num;
        try {
            num = Long.parseLong(Main.userInput.nextLine());
        } catch (NumberFormatException e) {
            num = -1;
        }
        return num;
    }
}
