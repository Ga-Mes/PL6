package command;

import command.server.ExitCommand;
import command.server.SaveCommand;
import data.CollectionManager;
import language.Lexer;
import net.Handler;
import org.slf4j.Logger;

import java.util.ArrayList;

public class CommandExecutor {
    private final boolean[] statuses;

    private final Logger logger;

    private final Handler handler;

    private final CollectionManager collectionManager;

    public CommandExecutor(boolean[] statuses, Logger logger, String fileName) throws Exception {
        this.statuses = statuses;
        this.logger = logger;

        handler = new Handler(logger);

        if (!handler.bind()) {
            throw new Exception();
        }

        collectionManager = new CollectionManager(logger, fileName);
    }

    public void execute(String input) {
        ArrayList<Object> compiled = Lexer.compile(input, logger);

        if (compiled == null) {
            return;
        }

        CommandType type = (CommandType) compiled.get(0);

        compiled.remove(0);

        switch (type) {
            case EXIT -> new ExitCommand().execute(statuses, compiled, logger, collectionManager);
            case SAVE -> new SaveCommand().execute(statuses, compiled, logger, collectionManager);
        }
    }

    public void tick() {
        handler.tick(statuses, logger, collectionManager);
    }
}
