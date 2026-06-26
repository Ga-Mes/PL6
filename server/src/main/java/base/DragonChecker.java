package base;

import java.util.ArrayList;
import java.util.TreeMap;

public class DragonChecker {
    public static Dragon form(DragonTemplate template, ArrayList<Object> primitives, int i) {
        return new Dragon((String) primitives.get(i++), template.getCoordinates(), (Integer) primitives.get(i++), (String) primitives.get(i), template.getColor(), template.getCharacter(), template.getCave(), new TreeMap<>());
    }
}
