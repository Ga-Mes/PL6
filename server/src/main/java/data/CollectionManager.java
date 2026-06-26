package data;

import base.Dragon;
import org.slf4j.Logger;

import java.util.*;

public class CollectionManager {
    public final TreeMap<Integer, Dragon> dragons;

    private final Date initDate = new Date();

    private final DatabaseManager databaseManager;

    private Map<String, HashSet<Integer>> ownerships = new HashMap<>();

    public CollectionManager(Logger logger) throws Exception {
        databaseManager = new DatabaseManager(logger);

        dragons = databaseManager.load(ownerships);
    }

    public void save() {

    }

    @Override
    public String toString() {
        return "Initialized: " + initDate + "\n" + "Type: " + Dragon.class + "\n" + "Count: " + dragons.size();
    }
}
