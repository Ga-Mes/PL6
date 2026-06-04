package command;

import command.concrete.ExitCommand;
import command.concrete.HelpCommand;
import command.concrete.InsertCommand;
import command.concrete.PortCommand;
import language.Lexer;
import net.Handler;
import org.jline.terminal.Terminal;

import java.util.ArrayList;

public class CommandExecutor {
    private final boolean[] statuses;

    private final Handler handler = new Handler();

    private final Terminal terminal;

    public CommandExecutor(boolean[] statuses, Terminal terminal) {
        this.statuses = statuses;
        this.terminal = terminal;
    }

    public void execute(String input) {
        ArrayList<Object> compiled = Lexer.compile(input);

        if (compiled == null) {
            return;
        }

        CommandType type = (CommandType) compiled.get(0);

        compiled.remove(0);

        switch (type) {
            case EXIT -> new ExitCommand().execute(statuses, handler, compiled, terminal);
            case PORT -> new PortCommand().execute(statuses, handler, compiled, terminal);
            case HELP -> new HelpCommand().execute(statuses, handler, compiled, terminal);
            case INSERT -> new InsertCommand().execute(statuses, handler, compiled, terminal);
        }
    }
}
