package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class PacketManager {
    private final int BUFFER_SIZE = 1400;

    private DatagramSocket socket;

    public Response send(String serialized) throws IOException {
        byte[] buffer = serialized.getBytes(StandardCharsets.UTF_8);

        int numberOfPackets = (int) Math.ceil((double) buffer.length / BUFFER_SIZE);

        for (int i = 0; i < numberOfPackets; i++) {
            byte[] sBuffer = new byte[2 + BUFFER_SIZE];

            sBuffer[0] = (byte) numberOfPackets;

            sBuffer[1] = (byte) i;

            System.arraycopy(buffer, BUFFER_SIZE * i, sBuffer, 2, Math.min(buffer.length, BUFFER_SIZE));

            DatagramPacket packet = new DatagramPacket(sBuffer, sBuffer.length);

            socket.send(packet);
        }

        return new Response(0, "Text...");
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramSocket getSocket() {
        return socket;
    }
}
