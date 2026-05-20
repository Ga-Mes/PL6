package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PacketManager {
    private final int BUFFER_SIZE = 1400;

    private DatagramSocket socket;

    public Response send(String serialized, InetSocketAddress address) throws IOException {
        byte[] buffer = serialized.getBytes(StandardCharsets.UTF_8);

        int numberOfPackets = (int) Math.ceil((double) buffer.length / BUFFER_SIZE);

        ArrayList<DatagramPacket> packets = new ArrayList<>();

        for (int i = 0; i < numberOfPackets; i++) {
            int payloadSize = Math.min(buffer.length - BUFFER_SIZE * i, BUFFER_SIZE);

            byte[] sBuffer = new byte[2 + payloadSize];

            sBuffer[0] = (byte) numberOfPackets;

            sBuffer[1] = (byte) i;

            System.arraycopy(buffer, BUFFER_SIZE * i, sBuffer, 2, payloadSize);

            DatagramPacket packet = new DatagramPacket(sBuffer, sBuffer.length, address);

            packets.add(packet);
        }

        HashSet<Integer> toAsk = IntStream.range(0, numberOfPackets).boxed().collect(Collectors.toCollection(HashSet::new));

        return new Response(0, "Text...");
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramSocket getSocket() {
        return socket;
    }
}
