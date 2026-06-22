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

public class Handler {
    private DatagramChannel channel = null;

    private final int BUFFER_SIZE = 1024;

    private final Logger logger;

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

            UUID uuid = new UUID(buffer.getLong(), buffer.getLong());

            int numberOfPackets = buffer.getInt();

            int type = buffer.getInt();

            int index = buffer.getInt();

            listeners.put(uuid, address);

            if (type == 0) {
                toAssemble.putIfAbsent(uuid, new TreeMap<>());

                byte[] data = new byte[buffer.remaining()];

                buffer.get(data);

                toAssemble.get(uuid).put(index, data);

                if (numberOfPackets == toAssemble.get(uuid).size()) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();

                    for (byte[] bytes : toAssemble.get(uuid).values()) {
                        out.write(bytes);
                    }

                    Request request = XMLWorker.parse(
                            out.toString(StandardCharsets.UTF_8),
                            Request.class
                    );

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

                    Response response = contexts.get(uuid).handle(statuses, logger, collectionManager, request);

                    prepare(response, uuid);

                    if (contexts.get(uuid).status == RequestStatus.FINISHED) {
                        contexts.remove(uuid);

                        logger.info("Finished: {}", uuid);
                    }
                }

                ByteBuffer aBuffer = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES * 3);

                aBuffer.putLong(uuid.getMostSignificantBits());
                aBuffer.putLong(uuid.getLeastSignificantBits());
                aBuffer.putInt(numberOfPackets);
                aBuffer.putInt(1);
                aBuffer.putInt(index);

                aBuffer.flip();

                channel.send(aBuffer, address);
            } else if (type == 1) {
                TreeMap<Integer, byte[]> packets = toSend.get(uuid);

                if (packets != null) {
                    packets.remove(index);

                    if (packets.isEmpty()) {
                        toSend.remove(uuid);
                        frameSizes.remove(uuid);
                        listeners.remove(uuid);

                        logger.info("Processed: {}", uuid);
                    }
                }
            }
        } catch (IOException ignored) {}

        for (UUID request : new ArrayList<>(toSend.keySet())) {
            try {
                for (Map.Entry<Integer, byte[]> entry :
                        toSend.get(request).entrySet()) {

                    ByteBuffer sBuffer = ByteBuffer.allocate(
                            Long.BYTES * 2 +
                                    Integer.BYTES * 3 +
                                    BUFFER_SIZE
                    );

                    sBuffer.putLong(request.getMostSignificantBits());
                    sBuffer.putLong(request.getLeastSignificantBits());
                    sBuffer.putInt(frameSizes.get(request));
                    sBuffer.putInt(0);
                    sBuffer.putInt(entry.getKey());
                    sBuffer.put(entry.getValue());

                    sBuffer.flip();

                    channel.send(
                            sBuffer,
                            listeners.get(request)
                    );
                }
            } catch (IOException ignored) {}
        }
    }

    private void prepare(Response response, UUID uuid) throws JacksonException {
        toAssemble.remove(uuid);

        toSend.put(uuid, new TreeMap<>());

        byte[] serialized = XMLWorker.serialize(response).getBytes(StandardCharsets.UTF_8);

        int numberOfPackets = (int) Math.ceil((double) serialized.length / BUFFER_SIZE);

        frameSizes.put(uuid, numberOfPackets);

        for (int i = 0; i < numberOfPackets; i++) {
            toSend.get(uuid).put(i, Arrays.copyOfRange(serialized, i * BUFFER_SIZE, Math.min((i + 1) * BUFFER_SIZE, serialized.length)));
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
