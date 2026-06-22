package command;

import data.CollectionManager;
import net.Request;
import net.RequestContext;
import net.Response;
import org.slf4j.Logger;

public abstract class AbstractClientCommand {
    public abstract Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request);
}
