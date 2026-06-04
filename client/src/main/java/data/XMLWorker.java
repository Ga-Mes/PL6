package data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XMLWorker {
    private static final XmlMapper mapper = new XmlMapper();

    public static String serialize(Object object) throws JsonProcessingException {
        return mapper.writeValueAsString(object);
    }
}
