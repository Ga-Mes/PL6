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

    @Override
    public String toString() {
        return "Initialized: " + initDate + "\n" + "Type: " + Dragon.class + "\n" + "Count: " + dragons.size();
    }
}
