package net;

import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;

public class Handler {
    private DatagramChannel channel = null;

    private final Logger logger;

    public Handler(Logger logger) {
        this.logger = logger;
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
