package command;

import data.CollectionManager;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;

import java.util.ArrayList;

public abstract class AbstractCommand {
    public abstract void execute(boolean[] statuses, ArrayList<Object> args, Terminal terminal, Logger logger, CollectionManager collectionManager);
}
