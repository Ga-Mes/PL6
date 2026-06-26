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

public class RegisterCommand extends AbstractClientCommand {
    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        Response response;

        ArrayList<Object> primitives;

        if ((primitives = Lexer.transform(request)) != null) {
            String login = (String) primitives.get(0);

            String password = (String) primitives.get(1);

            if (collectionManager.register(login, password)) {
                response = new Response(1, "Successfully registered!");
            } else {
                response = new Response(2, "Couldn't register. Try again with another login...");
            }
        } else {
            response = new Response(2, "Wrong primitive arguments...");
        }

        context.status = RequestStatus.FINISHED;

        return response;
    }
}
