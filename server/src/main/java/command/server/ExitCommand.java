package command.server;

import command.AbstractCommand;
import data.CollectionManager;
import net.Handler;
import org.slf4j.Logger;

import java.util.ArrayList;

public class ExitCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, ArrayList<Object> args, Logger logger, CollectionManager collectionManager, Handler handler) {
        statuses[0] = false;

        collectionManager.save();

        handler.close();

        logger.info("Finishing work...");
    }
}
