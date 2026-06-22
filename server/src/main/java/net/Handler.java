package net;

import data.CollectionManager;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.UUID;

public class Handler {
    private DatagramChannel channel = null;

    private final int BUFFER_SIZE = 1024;

    private final Logger logger;

    public Handler(Logger logger) {
        this.logger = logger;
    }

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
