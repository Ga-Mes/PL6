import ch.qos.logback.classic.spi.ILoggingEvent;
import console.ConsoleReader;
import console.LogBuffer;

import static console.ConsoleReader.renderMessage;

public class Server {
    public static void main(String[] args) {
        try {
            ConsoleReader reader = new ConsoleReader();

            reader.start();
        } catch (Exception ignored) {}

        ILoggingEvent event;

        while ((event = LogBuffer.getQueue().poll()) != null) {
            System.out.println("\r\033[K" + renderMessage(event));
        }
    }
}
