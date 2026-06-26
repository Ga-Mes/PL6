package command;

import data.CollectionManager;
import net.Handler;
import org.slf4j.Logger;

import java.util.ArrayList;

public abstract class AbstractCommand {
    public abstract void execute(boolean[] statuses, ArrayList<Object> args, Logger logger, CollectionManager collectionManager, Handler handler);
}
