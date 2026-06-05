package data;

import org.slf4j.Logger;

import java.util.TreeMap;

public class FileManager {
    private final Logger logger;

    public FileManager(Logger logger) {
        this.logger = logger;
    }

    public void load(TreeMap<Integer, Object> dragons) {
        logger.info("Loading collection...");
    }

    public void save() {
        logger.info("Saving...");
    }
}
