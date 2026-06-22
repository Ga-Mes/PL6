package net;

import command.AbstractClientCommand;
import command.client.HelpCommand;
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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class Handler {
    private DatagramChannel channel = null;

    private final int BUFFER_SIZE = 1024;

    private final Logger logger;

    public Handler(Logger logger) {
        this.logger = logger;
    }

    private final Map<UUID, TreeMap<Integer, byte[]>> toAssemble = new HashMap<>();

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

            logger.info("{} {} {} {}", uuid, numberOfPackets, type, index);

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
                            case INFO -> null;
                            case SHOW -> null;
                            case INSERT -> null;
                            case UPDATE -> null;
                            case REMOVE_KEY -> null;
                            case CLEAR -> null;
                            case EXECUTE_SCRIPT -> null;
                            case EXIT -> null;
                            case REMOVE_GREATER -> null;
                            case REPLACE_IF_GREATER -> null;
                            case REMOVE_GREATER_KEY -> null;
                            case FILTER_CONTAINS_DESCRIPTION -> null;
                            case FILTER_GREATER_THAN_AGE -> null;
                            case PRINT_UNIQUE_COLOR -> null;
                            case SAVE -> null;
                        };

                        contexts.put(uuid, new RequestContext(command));
                    }

                    Response response = contexts.get(uuid).handle(statuses, logger, collectionManager, request);

                    logger.info(String.valueOf(response));
                }

                ByteBuffer aBuffer = ByteBuffer.allocate(Long.BYTES * 2 + Integer.BYTES * 3);

                aBuffer.putLong(uuid.getMostSignificantBits());
                aBuffer.putLong(uuid.getLeastSignificantBits());
                aBuffer.putInt(numberOfPackets);
                aBuffer.putInt(1);
                aBuffer.putInt(index);

                aBuffer.rewind();

                channel.send(aBuffer, address);
            }
        } catch (IOException ignored) {}
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
