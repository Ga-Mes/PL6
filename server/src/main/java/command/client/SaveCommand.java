package command.client;

import command.AbstractClientCommand;
import data.CollectionManager;
import net.Request;
import net.RequestContext;
import net.Response;
import org.slf4j.Logger;

public class SaveCommand extends AbstractClientCommand {
    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        return new Response(2, "You can't do that...");
    }
}
