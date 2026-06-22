package net;

import command.AbstractClientCommand;
import data.CollectionManager;
import org.slf4j.Logger;

public class RequestContext {
    public RequestStatus status = RequestStatus.RUNNING;

    private final AbstractClientCommand command;

    public RequestContext(AbstractClientCommand command) {
        this.command = command;
    }

    public Response handle(boolean[] statuses, Logger logger, CollectionManager collectionManager, Request request) {
        return command.execute(statuses, logger, collectionManager, this, request);
    }
}
