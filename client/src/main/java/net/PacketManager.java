package net;

import data.XMLWorker;

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

    public Response get(Request request, InetSocketAddress address) throws IOException {
        send(request, address);



        return new Response(1, "Text...");
    }

    private void send(Request request, InetSocketAddress address) throws IOException {
        byte[] buffer = XMLWorker.serialize(request).getBytes(StandardCharsets.UTF_8);

        int numberOfPackets = (int) Math.ceil((double) buffer.length / BUFFER_SIZE);

        ArrayList<DatagramPacket> packets = new ArrayList<>();

        UUID uuid = UUID.randomUUID();

        ByteBuffer headerBuffer = ByteBuffer.allocate(24);

        headerBuffer.putInt(0);

        headerBuffer.putLong(uuid.getMostSignificantBits());
        headerBuffer.putLong(uuid.getLeastSignificantBits());

        headerBuffer.putInt(numberOfPackets);

        byte[] header = headerBuffer.array();

        for (int i = 0; i < numberOfPackets; i++) {
            int payloadSize = Math.min(buffer.length - BUFFER_SIZE * i, BUFFER_SIZE);

            ByteBuffer packetBuffer = ByteBuffer.allocate(28 + payloadSize);

            packetBuffer.put(header);

            packetBuffer.putInt(i);

            packetBuffer.put(buffer, BUFFER_SIZE * i, payloadSize);

            byte[] sBuffer = packetBuffer.array();

            DatagramPacket packet = new DatagramPacket(sBuffer, sBuffer.length, address);

            packets.add(packet);
        }

        deliver(packets, uuid, address);
    }

    private void deliver(ArrayList<DatagramPacket> packets, UUID uuid, InetSocketAddress address) throws IOException {
        HashSet<Integer> toResend = IntStream.range(0, packets.size()).boxed().collect(Collectors.toCollection(HashSet::new));

        ByteBuffer askBuffer = ByteBuffer.allocate(20);

        askBuffer.putLong(uuid.getMostSignificantBits());
        askBuffer.putLong(uuid.getLeastSignificantBits());

        askBuffer.putInt(1);

        byte[] ask = askBuffer.array();

        DatagramPacket askPacket = new DatagramPacket(ask, ask.length, address);

        socket.setSoTimeout(4);

        while (!toResend.isEmpty()) {
            for (Integer i : toResend) {
                socket.send(packets.get(i));
            }

            socket.send(askPacket);
        }
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramSocket getSocket() {
        return socket;
    }
}
