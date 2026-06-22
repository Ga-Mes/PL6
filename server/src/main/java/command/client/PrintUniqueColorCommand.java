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

public class PrintUniqueColorCommand extends AbstractClientCommand {
    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        Response response;

        if (Lexer.transform(request) != null) {
            ArrayList<String> pieces = new ArrayList<>();

            for (Dragon dragon : collectionManager.dragons.values()) {
                if (dragon.getColor() != null) {
                    pieces.add(dragon.getId() + " - " + dragon.getColor().hashCode());
                }
            }

            response = new Response(1, String.join("\n", pieces));
        } else {
            response = new Response(2, "Wrong primitive arguments...");
        }

        context.status = RequestStatus.FINISHED;

        return response;
    }
}
