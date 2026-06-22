package data;

import base.Dragon;
import base.DragonWrapper;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeMap;

public class FileManager {
    private final Logger logger;

    private final Path path;

    public FileManager(Logger logger, String fileName) throws Exception {
        this.logger = logger;

        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            logger.error("File name is not valid. Program will abort...");

            throw new Exception();
        }

        this.path = path;
    }

    public TreeMap<Integer, Dragon> load() {
        logger.info("Loading collection...");

        TreeMap<Integer, Dragon> result = new TreeMap<>();

        if (Files.exists(path)) {
            try {
                result = XMLWorker.parse(Files.readString(path), DragonWrapper.class).collection;
            } catch (IOException e) {
                logger.error("Couldn't load collection. It will be empty...");
            }
        }

        return result;
    }

    public void save(TreeMap<Integer, Dragon> dragons) {
        logger.info("Saving...");

        DragonWrapper wrapper = new DragonWrapper(dragons);

        if (Files.exists(path)) {
            try {
                String result = XMLWorker.serialize(wrapper);

                Files.writeString(path, result);

                return;
            } catch (IOException ignored) {}
        }

        logger.error("Couldn't save collection to the file...");
    }
}
