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

    public CollectionManager(Logger logger) throws Exception {
        databaseManager = new DatabaseManager(logger);

        dragons = databaseManager.load();
    }

    public void save() {
        databaseManager.close();
    }

    public boolean register(String login, String password) {
        return databaseManager.register(login, password);
    }

    public boolean authorize(String login, String password) {
        return databaseManager.authorize(login, password);
    }

    public boolean insert(Integer key, String login, Dragon dragon) {
        boolean success;

        lock.writeLock().lock();

        try {
            success = databaseManager.insert(key, dragon, login, dragons);
        } finally {
            lock.writeLock().unlock();
        }

        return success;
    }

    public boolean removeKey(String login, Integer key) {
        boolean success;

        lock.writeLock().lock();

        try {
            success = databaseManager.removeKey(dragons, login, key);
        } finally {
            lock.writeLock().unlock();
        }

        return success;
    }

    public boolean clear(String login) {
        boolean success;

        lock.writeLock().lock();

        try {
            success = databaseManager.clear(dragons, login);
        } finally {
            lock.writeLock().unlock();
        }

        return success;
    }

    public TreeMap<Integer, Dragon> getCollectionSnapshot() {
        lock.readLock().lock();

        try {
            return new TreeMap<>(dragons);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();

        try {
            return "Initialized: " + initDate + "\n" + "Type: " + Dragon.class + "\n" + "Count: " + dragons.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
