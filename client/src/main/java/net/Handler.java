package net;

import base.DragonCreator;
import base.DragonTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jline.terminal.Terminal;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.UUID;

public class Handler {
    private final PacketManager manager = new PacketManager();

    public String login = "";

    public String password = "";

    public void setPort(int port) {
        try {
            manager.setNet(port);
        } catch (UnknownHostException e) {
            System.out.println("Couldn't resolve hostname...");
        } catch (SocketException e) {
            System.out.println("Couldn't provide socket for the app...");
        }
    }

    public String process(Request request, Terminal terminal) {
        if (!manager.isAvailable()) {
            return "Try setting port with command \"port\" before executing server commands...";
        }

        try {
            UUID uuid = UUID.randomUUID();

            while (true) {
                Response response = manager.get(request, uuid);

                if (response.status() == 0) {
                    DragonTemplate template = DragonCreator.create(terminal);

                    ArrayList<Object> args = new ArrayList<>();

                    args.add(template);

                    request = new Request(request.type(), args, login, password);
                } else {
                    return response.text();
                }
            }
        } catch (JsonProcessingException e) {
            return "Error while serialization, try again...";
        } catch (IOException e) {
            return "I/O exception in net...";
        }
    }
}
