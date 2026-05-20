import console.ConsoleReader;

import java.io.IOException;

public class Client {
    public static void main(String[] args) {
        try {
            ConsoleReader reader = new ConsoleReader();

            reader.start();
        } catch (IOException e) {
            System.out.println("Couldn't start app because of console error...");
        }
    }
}
