package data;

import base.Dragon;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CollectionManager {
    public final TreeMap<Integer, Dragon> dragons;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final Date initDate = new Date();

    private final DatabaseManager databaseManager;

    private final Map<String, HashSet<Integer>> ownerships = new HashMap<>();

    public CollectionManager(Logger logger) throws Exception {
        databaseManager = new DatabaseManager(logger);

        dragons = databaseManager.load(ownerships);
    }

    public void save() {
        databaseManager.close();
    }

    public boolean register(String login, String password) {
        if (!databaseManager.register(login, password)) return false;

        lock.writeLock().lock();

        try {
            ownerships.put(login, new HashSet<>());
        } finally {
            lock.writeLock().unlock();
        }

        return true;
    }

    @Override
    public String toString() {
        return "Initialized: " + initDate + "\n" + "Type: " + Dragon.class + "\n" + "Count: " + dragons.size();
    }
}
