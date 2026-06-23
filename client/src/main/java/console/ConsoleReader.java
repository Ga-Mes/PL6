package console;

import command.CommandExecutor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConsoleReader {
    private final Terminal terminal;

    private final boolean[] statuses = new boolean[]{true};

    private final ArrayList<Character> buffer = new ArrayList<>();

    private final CommandExecutor executor;

    public ConsoleReader() throws IOException {
        terminal = TerminalBuilder.builder().system(true).build();

        terminal.enterRawMode();

        executor = new CommandExecutor(statuses, terminal);
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
            } catch (IOException e) {
                System.out.println("\nAborting app due to console exception...");

                statuses[0] = false;
            }
        }

        System.out.print("\r\033[K");
    }
}
