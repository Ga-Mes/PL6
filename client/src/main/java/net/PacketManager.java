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

        return receive(uuid);
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

        int retries = 8;

        while (!packets.isEmpty() && (retries != 0)) {
            for (Integer ix : packets.keySet()) {
                socket.send(packets.get(ix));
            }

            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE * 2], BUFFER_SIZE * 2);

                    socket.receive(packet);

                    ByteBuffer buffer = ByteBuffer.wrap(packet.getData(), 0, packet.getLength());

                    if (!new UUID(buffer.getLong(), buffer.getLong()).equals(uuid)) continue;

                    if (buffer.getInt() != numberOfPackets) continue;

                    if (buffer.getInt() != 1) continue;

                    int index = buffer.getInt();

                    packets.remove(index);

                    if (packets.isEmpty()) break;
                }
            } catch (IOException ignored) {}

            retries--;
        }

        if (!packets.isEmpty()) throw new IOException();
    }

    private Response receive(UUID uuid) throws IOException {
        TreeMap<Integer, byte[]> packets = new TreeMap<>();

        int numberOfPackets = -1;

        int retries = 10;

        while ((packets.isEmpty() || packets.size() != numberOfPackets)
                && retries > 0) {
            try {
                DatagramPacket packet =
                        new DatagramPacket(
                                new byte[BUFFER_SIZE * 2],
                                BUFFER_SIZE * 2
                        );

                socket.receive(packet);

                ByteBuffer buffer =
                        ByteBuffer.wrap(
                                packet.getData(),
                                0,
                                packet.getLength()
                        );

                UUID packetUuid =
                        new UUID(
                                buffer.getLong(),
                                buffer.getLong()
                        );

                if (!packetUuid.equals(uuid)) {
                    continue;
                }

                int numOfPackets = buffer.getInt();

                if (numberOfPackets == -1) {
                    numberOfPackets = numOfPackets;
                } else if (numberOfPackets != numOfPackets) {
                    continue;
                }

                if (buffer.getInt() != 0) {
                    continue;
                }

                int index = buffer.getInt();

                byte[] payload = new byte[buffer.remaining()];
                buffer.get(payload);

                packets.put(index, payload);

                ByteBuffer ack = ByteBuffer.allocate(
                        Long.BYTES * 2 + Integer.BYTES * 3
                );

                ack.putLong(uuid.getMostSignificantBits());
                ack.putLong(uuid.getLeastSignificantBits());
                ack.putInt(numberOfPackets);
                ack.putInt(1);
                ack.putInt(index);

                DatagramPacket ackPacket = new DatagramPacket(
                        ack.array(),
                        ack.position(),
                        address
                );

                socket.send(ackPacket);

                retries = 10;

            } catch (SocketTimeoutException ignored) {
                retries--;
            }
        }

        if (packets.isEmpty()
                || numberOfPackets == -1
                || packets.size() != numberOfPackets) {
            throw new IOException("Response timeout...");
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (byte[] data : packets.values()) {
            out.write(data);
        }

        return XMLWorker.parse(
                out.toString(StandardCharsets.UTF_8),
                Response.class
        );
    }

    public void setNet(int port) throws UnknownHostException, SocketException {
        address = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), port);

        this.socket = new DatagramSocket();

        this.socket.setSoTimeout(1000);
    }

    public boolean isAvailable() {
        return (address != null) && (socket != null);
    }
}
