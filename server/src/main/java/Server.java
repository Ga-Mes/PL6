import ch.qos.logback.classic.spi.ILoggingEvent;
import console.ConsoleReader;
import console.LogBuffer;

import java.io.IOException;

import static console.ConsoleReader.renderMessage;

public class Server {
    public static void main(String[] args) {
        try {
            if (args.length != 0) {
                ConsoleReader reader = new ConsoleReader(args[0]);

                reader.start();
            } else {
                System.out.println("Couldn't start app because file name was not provided...");

                throw new Exception();
            }
        } catch (IOException e) {
            System.out.println("Couldn't start app because of console error...");
        } catch (Exception ignored) {}

        ILoggingEvent event;

        while ((event = LogBuffer.getQueue().poll()) != null) {
            System.out.println("\r\033[K" + renderMessage(event));
        }
    }
}
