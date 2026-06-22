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
import java.util.Set;
import java.util.stream.Collectors;

public class RemoveGreaterKeyCommand extends AbstractClientCommand {
    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        Response response;

        ArrayList<Object> primitives;

        if ((primitives = Lexer.transform(request)) != null) {
            Integer key = (Integer) primitives.get(0);

            Set<Integer> valid = collectionManager.dragons.keySet().stream().filter(integer -> integer > key).collect(Collectors.toSet());

            for (Integer i : valid) {
                collectionManager.dragons.remove(i);
            }

            return new Response(1, "Removed dragons in the collection...");
        } else {
            response = new Response(2, "Wrong primitive arguments...");
        }

        context.status = RequestStatus.FINISHED;

        return response;
    }
}
