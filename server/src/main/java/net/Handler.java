package net;

import com.fasterxml.jackson.core.JacksonException;
import command.AbstractClientCommand;
import command.client.*;
import data.CollectionManager;
import data.XMLWorker;
import org.slf4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Handler {
    private DatagramChannel channel = null;

    private final int BUFFER_SIZE = 1024;

    private final Logger logger;

    private final ExecutorService receivePool =
            Executors.newCachedThreadPool();

    private final ForkJoinPool processPool =
            new ForkJoinPool();

    private final ReadWriteLock lock =
            new ReentrantReadWriteLock();

    public Handler(Logger logger) {
        this.logger = logger;
    }

    private final Map<UUID, TreeMap<Integer, byte[]>> toAssemble = new HashMap<>();

    private final Map<UUID, TreeMap<Integer, byte[]>> toSend = new HashMap<>();

    private final Map<UUID, Integer> frameSizes = new HashMap<>();

    private final Map<UUID, InetSocketAddress> listeners = new HashMap<>();

    private final Map<UUID, RequestContext> contexts = new HashMap<>();

    public void tick(boolean[] statuses, Logger logger, CollectionManager collectionManager) {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE * 2);

        try {
            InetSocketAddress address = (InetSocketAddress) channel.receive(buffer);

            if (address == null) return;

            buffer.flip();

            receivePool.submit(() -> processPacket(buffer.duplicate(), address, statuses, logger, collectionManager));
        } catch (IOException ignored) {}
    }

    private void prepare(Response response, UUID uuid) throws JacksonException {
        lock.writeLock().lock();

        try {
            toAssemble.remove(uuid);

            toSend.put(uuid, new TreeMap<>());

            byte[] serialized = XMLWorker.serialize(response).getBytes(StandardCharsets.UTF_8);

            int numberOfPackets = (int) Math.ceil((double) serialized.length / BUFFER_SIZE);

            frameSizes.put(uuid, numberOfPackets);

            for (int i = 0; i < numberOfPackets; i++) {
                toSend.get(uuid).put(i, Arrays.copyOfRange(serialized, i * BUFFER_SIZE, Math.min((i + 1) * BUFFER_SIZE, serialized.length)));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void processPacket(ByteBuffer buffer, InetSocketAddress address, boolean[] statuses, Logger logger, CollectionManager collectionManager) {
        UUID uuid = new UUID(buffer.getLong(), buffer.getLong());

        int numberOfPackets = buffer.getInt();

        int type = buffer.getInt();

        int index = buffer.getInt();

        lock.writeLock().lock();

        try {
            listeners.put(uuid, address);
        } finally {
            lock.writeLock().unlock();
        }

        if (type == 0) {
            processDataPacket(uuid, numberOfPackets, index, buffer, statuses, logger, collectionManager, address);
        } else if (type == 1) {
            processAckPacket(uuid, index);
        }
    }

    private void processDataPacket(UUID uuid, int numberOfPackets, int index, ByteBuffer buffer, boolean[] statuses, Logger logger, CollectionManager collectionManager, InetSocketAddress address) {
        byte[] data = new byte[buffer.remaining()];

        buffer.get(data);

        TreeMap<Integer, byte[]> packets;

        lock.writeLock().lock();

        try {

            toAssemble.putIfAbsent(uuid, new TreeMap<>());

            toAssemble.get(uuid).put(index, data);

            if (toAssemble.get(uuid).size() != numberOfPackets)
                return;

            packets = new TreeMap<>(toAssemble.get(uuid));

        } finally {
            lock.writeLock().unlock();
        }

        try {
            ByteBuffer aBuffer = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES * 3);

            aBuffer.putLong(uuid.getMostSignificantBits());
            aBuffer.putLong(uuid.getLeastSignificantBits());
            aBuffer.putInt(numberOfPackets);
            aBuffer.putInt(1);
            aBuffer.putInt(index);

            aBuffer.flip();

            channel.send(aBuffer, address);
        } catch (IOException ignored) {}

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        for (byte[] bytes : packets.values()) {
            out.write(bytes, 0, bytes.length);
        }

        try {
            Request request = XMLWorker.parse(
                    out.toString(StandardCharsets.UTF_8),
                    Request.class
            );

            processPool.execute(() -> processRequest(request, uuid, statuses, logger, collectionManager));
        } catch (JacksonException e) {
            logger.error("Error parsing request: {}", uuid);
        }
    }

    private void processRequest(Request request, UUID uuid, boolean[] statuses, Logger logger, CollectionManager collectionManager) {
        //TODO: Check credentials

        RequestContext context;

        lock.writeLock().lock();

        try {
            if (!contexts.containsKey(uuid)) {
                AbstractClientCommand command = switch (request.type()) {
                    case HELP -> new HelpCommand();
                    case INFO -> new InfoCommand();
                    case SHOW -> new ShowCommand();
                    case INSERT -> new InsertCommand();
                    case UPDATE -> new UpdateCommand();
                    case REMOVE_KEY -> new RemoveKeyCommand();
                    case CLEAR -> new ClearCommand();
                    case EXIT -> new ExitCommand();
                    case REMOVE_GREATER -> new RemoveGreaterCommand();
                    case REMOVE_GREATER_KEY -> new RemoveGreaterKeyCommand();
                    case FILTER_CONTAINS_DESCRIPTION -> new FilterContainsDescription();
                    case FILTER_GREATER_THAN_AGE -> new FilterGreaterThanAgeCommand();
                    case PRINT_UNIQUE_COLOR -> new PrintUniqueColorCommand();
                    case SAVE -> new SaveCommand();
                };

                contexts.put(uuid, new RequestContext(command));

                logger.info("Started: {}", uuid);
            }

            context = contexts.get(uuid);
        } finally {
            lock.writeLock().unlock();
        }

        Response response = context.handle(statuses, logger, collectionManager, request);

        if (context.status == RequestStatus.FINISHED) {
            lock.writeLock().lock();

            try {
                contexts.remove(uuid);
            } finally {
                lock.writeLock().unlock();
            }

            logger.info("Finished: {}", uuid);
        }

        new Thread(() -> sendResponse(response, uuid)).start();
    }

    private void sendResponse(Response response, UUID uuid) {
        try {
            prepare(response, uuid);
        } catch (JacksonException e) {
            logger.error("Couldn't form a response...");

            return;
        }

        TreeMap<Integer, byte[]> packetsToSend;

        int frameSize;

        InetSocketAddress address;

        lock.readLock().lock();

        try {
            if (!toSend.containsKey(uuid)) return;

            packetsToSend = new TreeMap<>(toSend.get(uuid));

            address = listeners.get(uuid);

            frameSize = frameSizes.get(uuid);
        } finally {
            lock.readLock().unlock();
        }

        for (Map.Entry<Integer, byte[]> entry : packetsToSend.entrySet()) {
            try {
                ByteBuffer sBuffer = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES * 3 + BUFFER_SIZE);
                sBuffer.putLong(uuid.getMostSignificantBits());
                sBuffer.putLong(uuid.getLeastSignificantBits());
                sBuffer.putInt(frameSize);
                sBuffer.putInt(0);
                sBuffer.putInt(entry.getKey());
                sBuffer.put(entry.getValue());

                sBuffer.flip();

                channel.send(sBuffer, address);
            } catch (IOException e) {
                logger.error("Failed to send packet to {}", address);
            }
        }
    }

    private void processAckPacket(UUID uuid, int index) {
        lock.writeLock().lock();

        try {

            TreeMap<Integer, byte[]> packets =
                    toSend.get(uuid);

            if (packets != null) {

                packets.remove(index);

                if (packets.isEmpty()) {

                    toSend.remove(uuid);
                    frameSizes.remove(uuid);
                    listeners.remove(uuid);

                    logger.info("Processed: {}", uuid);
                }
            }

        } finally {

            lock.writeLock().unlock();

        }
    }

    public boolean bind() {
        try {
            channel = DatagramChannel.open();
        } catch (IOException e) {
            logger.error("Couldn't open a channel...");

            return false;
        }

        try {
            channel.configureBlocking(false);
        } catch (IOException e) {
            logger.error("Couldn't configure the channel...");

            return false;
        }

        int port = 1024;

        while (true) {
            try {
                InetSocketAddress address = new InetSocketAddress(port);

                channel.bind(address);

                break;
            } catch (UnknownHostException e) {
                logger.error("Unknown host...");

                return false;
            } catch (IOException e) {
                port++;
            }
        }

        logger.info("Port: {}", port);

        return true;
    }
}
