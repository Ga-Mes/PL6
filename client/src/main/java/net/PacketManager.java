package net;

import data.XMLWorker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class PacketManager {
    private InetSocketAddress address = null;

    private final int BUFFER_SIZE = 1024;

    private DatagramSocket socket;

    public Response get(Request request, UUID uuid) throws IOException {
        send(request, uuid);

        return receive();
    }

    private void send(Request request, UUID uuid) throws IOException {
        byte[] serialized = XMLWorker.serialize(request).getBytes(StandardCharsets.UTF_8);

        int numberOfPackets = (int) Math.ceil((double) serialized.length / BUFFER_SIZE);

        Map<Integer, DatagramPacket> packets = new HashMap<>();

        for (int i = 0; i < numberOfPackets; i++) {
            ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES * 3 + BUFFER_SIZE);

            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            buffer.putInt(numberOfPackets);
            buffer.putInt(0);
            buffer.putInt(i);

            buffer.put(serialized, i * BUFFER_SIZE, Math.min(BUFFER_SIZE, serialized.length - i * BUFFER_SIZE));

            int size = buffer.position();

            byte[] data = Arrays.copyOf(buffer.array(), size);

            DatagramPacket packet = new DatagramPacket(
                    data,
                    data.length,
                    address
            );

            packets.put(i, packet);
        }

        while (!packets.isEmpty()) {
            for (Integer ix : packets.keySet()) {
                socket.send(packets.get(ix));
            }

            byte[] temporaryBuffer = new byte[BUFFER_SIZE * 2];

            DatagramPacket packet = new DatagramPacket(temporaryBuffer, temporaryBuffer.length);

            socket.receive(packet);

            ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());

            buffer.getLong();
            buffer.getLong();
            buffer.getInt();
            buffer.getInt();

            int index = buffer.getInt();

            packets.remove(index);
        }
    }

    private Response receive() throws IOException {
        TreeMap<Integer, byte[]> packets = new TreeMap<>();

        int numberOfPackets = -1;

        while ((packets.isEmpty()) || (numberOfPackets != packets.size())) {
            byte[] temporaryBuffer = new byte[BUFFER_SIZE * 2];

            DatagramPacket packet = new DatagramPacket(temporaryBuffer, temporaryBuffer.length);

            socket.receive(packet);

            ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());

            UUID uuid = new UUID(buffer.getLong(), buffer.getLong());

            numberOfPackets = buffer.getInt();

            buffer.getInt();

            int index = buffer.getInt();

            byte[] payload = new byte[buffer.remaining()];

            buffer.get(payload);

            packets.put(index, payload);

            buffer = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES * 3 + BUFFER_SIZE);

            buffer.putLong(uuid.getMostSignificantBits());
            buffer.putLong(uuid.getLeastSignificantBits());
            buffer.putInt(numberOfPackets);
            buffer.putInt(1);
            buffer.putInt(index);

            int size = buffer.position();

            byte[] data = Arrays.copyOf(buffer.array(), size);

            DatagramPacket temporaryPacket = new DatagramPacket(
                    data,
                    data.length,
                    address
            );

            socket.send(temporaryPacket);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        packets.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
            try {
                out.write(e.getValue());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        return XMLWorker.parse(out.toString(StandardCharsets.UTF_8), Response.class);
    }

    public void setNet(int port) throws UnknownHostException, SocketException {
        address = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port);

        this.socket = new DatagramSocket();

        this.socket.setSoTimeout(2000);
    }

    public boolean isAvailable() {
        return (address != null) && (socket != null);
    }
}
