package command;

import command.concrete.ExitCommand;
import command.concrete.SaveCommand;
import data.CollectionManager;
import language.Lexer;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;

import java.util.ArrayList;

public class CommandExecutor {
    private final boolean[] statuses;

    private final Terminal terminal;

    private final Logger logger;

    private final CollectionManager collectionManager;

    public CommandExecutor(boolean[] statuses, Terminal terminal, Logger logger) {
        this.statuses = statuses;
        this.terminal = terminal;
        this.logger = logger;

        collectionManager = new CollectionManager(logger);
    }

    public void execute(String input) {
        ArrayList<Object> compiled = Lexer.compile(input, logger);

        if (compiled == null) {
            return;
        }

        CommandType type = (CommandType) compiled.get(0);

        compiled.remove(0);

        switch (type) {
            case EXIT -> new ExitCommand().execute(statuses, compiled, terminal, logger, collectionManager);
            case SAVE -> new SaveCommand().execute(statuses, compiled, terminal, logger, collectionManager);
        }
    }

    public void tick() {
        // TODO: handle net
    }
}
