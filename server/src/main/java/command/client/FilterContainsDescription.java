package command.client;

import base.Dragon;
import command.AbstractClientCommand;
import data.CollectionManager;
import language.Lexer;
import net.Request;
import net.RequestContext;
import net.RequestStatus;
import net.Response;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

public class FilterContainsDescription extends AbstractClientCommand {
    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        Response response;

        ArrayList<Object> primitives;

        if ((primitives = Lexer.transform(request)) != null) {
            String description = (String) primitives.get(0);

            Set<String> valid = collectionManager.dragons.values().stream().filter((dragon -> dragon.getDescription().contains(description))).map(Dragon::toString).collect(Collectors.toSet());

            response = new Response(1, String.join("\n", valid));
        } else {
            response = new Response(2, "Wrong primitive arguments...");
        }

        context.status = RequestStatus.FINISHED;

        return response;
    }
}
