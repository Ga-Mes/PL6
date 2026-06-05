package command.concrete;

import command.AbstractCommand;
import data.CollectionManager;
import net.Handler;
import org.jline.terminal.Terminal;
import org.slf4j.Logger;

import java.util.ArrayList;

public class ExitCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, Handler handler, ArrayList<Object> args, Terminal terminal, Logger logger, CollectionManager collectionManager) {
        statuses[0] = false;

        logger.info("Finishing work...");
    }
}
