package command.client;

import command.AbstractClientCommand;
import data.CollectionManager;
import language.Lexer;
import net.Request;
import net.RequestContext;
import net.RequestStatus;
import net.Response;
import org.slf4j.Logger;

public class HelpCommand extends AbstractClientCommand {
    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        Response response;

        if (Lexer.transform(request) != null) {
            response = new Response(1, "List of commands...");
        } else {
            response = new Response(2, "Wrong primitive arguments...");
        }

        context.status = RequestStatus.FINISHED;

        return response;
    }
}
