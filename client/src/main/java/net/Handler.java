package net;

import com.fasterxml.jackson.core.JsonProcessingException;
import data.XMLWorker;

import java.io.IOException;
import java.net.*;

public class Handler {
    private InetSocketAddress address = null;

    private final PacketManager manager = new PacketManager();

    public void setPort(int port) {
        try {
            address = new InetSocketAddress(InetAddress.getByName("helios"), port);

            manager.setSocket(new DatagramSocket());
        } catch (UnknownHostException e) {
            System.out.println("Couldn't resolve hostname...");
        } catch (SocketException e) {
            System.out.println("Couldn't provide socket for the app...");
        }
    }

    public String process(Request request) {
        if ((address == null) || (manager.getSocket() == null)) {
            return "Try setting port with command \"port\" before executing server commands...";
        }

        try {
            while (true) {
                Response response = manager.send(XMLWorker.serialize(request));

                return response.status() + " - " + response.text();
            }
        } catch (JsonProcessingException e) {
            return "Error while serialization, try again...";
        } catch (IOException e) {
            return "I/O exception in net...";
        }
    }
}
