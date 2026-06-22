package data;

import base.Dragon;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

public class FileManager {
    private final Logger logger;

    private final String fileName;

    public FileManager(Logger logger, String fileName) throws Exception {
        this.logger = logger;
        this.fileName = fileName;

        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            logger.error("File name is not valid. Program will abort...");

            throw new Exception();
        }
    }

    public void load(TreeMap<Integer, Dragon> dragons) {
        logger.info("Loading collection...");
    }

    public void save() {
        logger.info("Saving...");
    }
}
