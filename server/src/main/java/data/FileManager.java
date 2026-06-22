package data;

import base.Dragon;
import org.slf4j.Logger;

import java.util.TreeMap;

public class FileManager {
    private final Logger logger;

    public FileManager(Logger logger) {
        this.logger = logger;
    }

    public void load(TreeMap<Integer, Dragon> dragons) {
        logger.info("Loading collection...");
    }

    public void save() {
        logger.info("Saving...");
    }
}
