package console;

import ch.qos.logback.classic.spi.ILoggingEvent;
import command.CommandExecutor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConsoleReader {
    private final Terminal terminal;

    private final boolean[] statuses = new boolean[]{true};

    private final ArrayList<Character> buffer = new ArrayList<>();

    private final CommandExecutor executor;

    private final Logger logger = LoggerFactory.getLogger(ConsoleReader.class);

    public ConsoleReader() throws Exception {
        try {
            terminal = TerminalBuilder.builder().system(true).build();
        } catch (IOException e) {
            logger.error("Couldn't start terminal...");

            throw new Exception();
        }

        terminal.enterRawMode();

        executor = new CommandExecutor(statuses, logger);

        logger.info("Starting...");
    }

    public void start() {
        NonBlockingReader reader = terminal.reader();

        System.out.print("\r\033[K> ");

        while (statuses[0]) {
            try {
                int ch;

                if ((ch = reader.read(10L)) >= 0) {
                    if ((ch == 8 || ch == 127) && !buffer.isEmpty()) {
                        buffer.remove(buffer.size() - 1);
                    } else if ((ch == '\n' || ch == '\r') && !buffer.isEmpty()) {
                        String input = buffer.stream().map(String::valueOf).collect(Collectors.joining());

                        System.out.print('\n');

                        executor.execute(input);

                        buffer.clear();
                    } else if (!Character.isISOControl(ch)) {
                        buffer.add((char) ch);
                    }

                    System.out.print("\r\033[K> " + buffer.stream().map(String::valueOf).collect(Collectors.joining()));
                }

                ILoggingEvent event;

                while ((event = LogBuffer.getQueue().poll()) != null) {
                    System.out.println("\r\033[K" + renderMessage(event));

                    System.out.print("\r\033[K> ");
                }

                executor.tick();
            } catch (IOException e) {
                logger.error("\nAborting app due to console exception...");

                statuses[0] = false;
            }
        }

        System.out.print("\r\033[K");
    }

    public static String renderMessage(ILoggingEvent event) {
        String dateTime = Instant.ofEpochMilli(event.getTimeStamp()).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return "[" + dateTime + " / " + event.getLevel() + "] " + event.getFormattedMessage();
    }
}
