package data;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XMLWorker {
    private static final XmlMapper mapper = new XmlMapper();

    public static String serialize(Object object) throws JacksonException {
        return mapper.writeValueAsString(object);
    }

    public static <T> T parse(String xml, Class<T> clazz) throws JacksonException {
        return mapper.readValue(xml, clazz);
    }
}
