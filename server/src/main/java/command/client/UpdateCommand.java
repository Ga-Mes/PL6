package command.client;

import base.Dragon;
import base.DragonChecker;
import base.DragonTemplate;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import command.AbstractClientCommand;
import data.CollectionManager;
import language.Lexer;
import net.Request;
import net.RequestContext;
import net.RequestStatus;
import net.Response;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Objects;

enum UpdateCommandStatus {
    START, PENDING_DRAGON
}

public class UpdateCommand extends AbstractClientCommand {
    private UpdateCommandStatus status = UpdateCommandStatus.START;

    private ArrayList<Object> primitives = new ArrayList<>();

    @Override
    public Response execute(boolean[] statuses, Logger logger, CollectionManager collectionManager, RequestContext context, Request request) {
        try {
            switch (status) {
                case START -> {
                    if ((primitives = Lexer.transform(request)) != null) {
                        status = UpdateCommandStatus.PENDING_DRAGON;

                        return new Response(0, "Please fill the form...");
                    } else {
                        return new Response(2, "Wrong primitive arguments...");
                    }
                }
                case PENDING_DRAGON -> {
                    Integer id = (Integer) primitives.get(0);

                    ObjectMapper mapper = new ObjectMapper();

                    mapper.enable(
                            DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT
                    );

                    DragonTemplate template = mapper.convertValue(request.items().get(0), DragonTemplate.class);

                    Dragon dragon = DragonChecker.form(template, primitives, 1, collectionManager);

                    for (Dragon cDragon : collectionManager.dragons.values()) {
                        if (Objects.equals(cDragon.getId(), id)) {
                            cDragon.exchange(dragon);

                            break;
                        }
                    }

                    context.status = RequestStatus.FINISHED;

                    return new Response(1, "Updated dragon in the collection...");
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        context.status = RequestStatus.FINISHED;

        return new Response(2, "Error while handling command...");
    }
}
