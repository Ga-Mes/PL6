package data;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XMLWorker {

    private static final XmlMapper mapper = new XmlMapper();

    static {
        mapper.findAndRegisterModules();
    }

    public static <T> String serialize(T object) throws JacksonException {
        return mapper.writeValueAsString(object);
    }

    public static <T> T parse(String xml, Class<T> clazz) throws JacksonException {
        return mapper.readValue(xml, clazz);
    }
}
