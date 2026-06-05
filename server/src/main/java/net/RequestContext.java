package net;

public class RequestContext {
    private Request request;

    private Response response;

    private RequestStatus status = RequestStatus.RUNNING;

    public void handle(Request request) {
        this.request = request;
    }

    public void handle() {

    }
}
