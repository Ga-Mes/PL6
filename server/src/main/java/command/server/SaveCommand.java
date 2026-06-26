package command.server;

import command.AbstractCommand;
import data.CollectionManager;
import net.Handler;
import org.slf4j.Logger;

import java.util.ArrayList;

public class SaveCommand extends AbstractCommand {
    @Override
    public void execute(boolean[] statuses, ArrayList<Object> args, Logger logger, CollectionManager collectionManager, Handler handler) {
        collectionManager.save();
    }
}
