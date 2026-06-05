package console;

import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogBuffer {
    private final static Queue<ILoggingEvent> queue = new ConcurrentLinkedQueue<>();

    public static Queue<ILoggingEvent> getQueue() {
        return queue;
    }
}
