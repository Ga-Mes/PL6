package command.client;

import command.AbstractClientCommand;
import data.CollectionManager;
import net.Request;
import net.RequestContext;
import net.RequestStatus;
import net.Response;
import org.slf4j.Logger;

public class HelpCommand extends AbstractClientCommand {
    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        Response response;

        if (request.items().isEmpty()) {
            response = new Response(1, "List of commands...");
        } else {
            response = new Response(2, "Wrong number of arguments...");
        }

        context.status = RequestStatus.FINISHED;

        return response;
    }
}
