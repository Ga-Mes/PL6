package command.client;

import command.AbstractClientCommand;
import data.CollectionManager;
import language.Lexer;
import net.Request;
import net.RequestContext;
import net.RequestStatus;
import net.Response;
import org.slf4j.Logger;

import java.util.ArrayList;

public class RemoveKeyCommand extends AbstractClientCommand {

    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        Response response;

        ArrayList<Object> primitives;

        if ((primitives = Lexer.transform(request)) != null) {
            if (collectionManager.removeKey(request.login(), (Integer) primitives.get(0))) {
                response = new Response(1, "Removed element...");
            } else {
                response = new Response(2, "Couldn't remove the element...");
            }
        } else {
            response = new Response(2, "Wrong primitive arguments...");
        }

        context.status = RequestStatus.FINISHED;

        return response;
    }
}
