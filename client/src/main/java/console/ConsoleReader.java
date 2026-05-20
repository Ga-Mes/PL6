package console;

import command.CommandExecutor;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ConsoleReader {
    private final Terminal terminal;

    private final boolean[] statuses = new boolean[]{true};

    private final ArrayList<Character> buffer = new ArrayList<>();

    private String lastRender = "";

    private final CommandExecutor executor;

    public ConsoleReader() throws IOException {
        terminal = TerminalBuilder.builder().system(true).jna(true).jansi(true).build();

        terminal.enterRawMode();

        executor = new CommandExecutor(statuses, terminal);
    }

    public void start() {
        System.out.print("> ");

        while (statuses[0]) {
            try {
                if (terminal.reader().ready()) {
                    int ch = terminal.reader().read();

                    if (ch == 8 && !buffer.isEmpty()) {
                        buffer.remove(buffer.size() - 1);
                    }

                    if ((ch == '\n' || ch == '\r') && !buffer.isEmpty()) {
                        String input = buffer.stream().map(String::valueOf).collect(Collectors.joining());

                        System.out.print("\n");

                        executor.execute(input);

                        buffer.clear();

                        continue;
                    }

                    if (!Character.isISOControl(ch)) {
                        buffer.add((char) ch);
                    }
                }

                if (lastRender.length() != buffer.size()) {
                    String line = buffer.stream().map(String::valueOf).collect(Collectors.joining());

                    lastRender = line;

                    System.out.print("\r\033[K> " + line);
                }
            } catch (IOException e) {
                System.out.println("\nAborting app due to console exception...");

                statuses[0] = false;
            }
        }
    }
}
