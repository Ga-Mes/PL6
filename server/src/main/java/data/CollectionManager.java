package data;

import base.Dragon;
import org.slf4j.Logger;

import java.util.TreeMap;

public class CollectionManager {
    public final TreeMap<Integer, Dragon> dragons = new TreeMap<>();

    private final FileManager fileManager;

    public CollectionManager(Logger logger) {
        fileManager = new FileManager(logger);

        fileManager.load(dragons);
    }

    public void save() {
        fileManager.save();
    }
}
