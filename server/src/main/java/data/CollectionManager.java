package data;

import base.Dragon;
import org.slf4j.Logger;

import java.util.Date;
import java.util.TreeMap;

public class CollectionManager {
    public final TreeMap<Integer, Dragon> dragons;

    private final Date initDate = new Date();

    private final FileManager fileManager;

    public CollectionManager(Logger logger, String fileName) throws Exception {
        fileManager = new FileManager(logger, fileName);

        dragons = fileManager.load();
    }

    public void save() {
        fileManager.save(dragons);
    }

    @Override
    public String toString() {
        return "Initialized: " + initDate + "\n" + "Type: " + Dragon.class + "\n" + "Count: " + dragons.size();
    }
}
