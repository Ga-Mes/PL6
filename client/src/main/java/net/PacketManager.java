package net;

import com.fasterxml.jackson.core.JsonProcessingException;
import data.XMLWorker;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class PacketManager {
    private InetSocketAddress address = null;

    private final int BUFFER_SIZE = 1400;

    private DatagramSocket socket;

    public Response get(Request request) throws IOException {
        send(request);

        return new Response(1, "Text...");
    }

    private void send(Request request) throws JsonProcessingException {
        byte[] serialized = XMLWorker.serialize(request).getBytes(StandardCharsets.UTF_8);
    }

    public void setNet(int port) throws UnknownHostException, SocketException {
        address = new InetSocketAddress(InetAddress.getByName("helios"), port);

        this.socket = new DatagramSocket();
    }

    public boolean isAvailable() {
        return (address != null) && (socket != null);
    }
}
