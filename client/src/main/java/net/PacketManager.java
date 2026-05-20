package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PacketManager {
    private final int BUFFER_SIZE = 1400;

    private DatagramSocket socket;

    public Response send(String serialized, InetSocketAddress address) throws IOException {
        byte[] buffer = serialized.getBytes(StandardCharsets.UTF_8);

        int numberOfPackets = (int) Math.ceil((double) buffer.length / BUFFER_SIZE);

        ArrayList<DatagramPacket> packets = new ArrayList<>();

        UUID uuid = UUID.randomUUID();

        ByteBuffer headerBuffer = ByteBuffer.allocate(20);

        headerBuffer.putLong(uuid.getMostSignificantBits());
        headerBuffer.putLong(uuid.getLeastSignificantBits());

        headerBuffer.putInt(numberOfPackets);

        byte[] header = headerBuffer.array();

        for (int i = 0; i < numberOfPackets; i++) {
            int payloadSize = Math.min(buffer.length - BUFFER_SIZE * i, BUFFER_SIZE);

            ByteBuffer packetBuffer = ByteBuffer.allocate(24 + payloadSize);

            packetBuffer.put(header);

            packetBuffer.putInt(i);

            packetBuffer.put(buffer, BUFFER_SIZE * i, payloadSize);

            byte[] sBuffer = packetBuffer.array();

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
