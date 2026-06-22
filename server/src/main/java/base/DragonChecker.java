package base;

import data.CollectionManager;

import java.util.ArrayList;

public class DragonChecker {
    public static Dragon form(DragonTemplate template, ArrayList<Object> primitives, int i, CollectionManager manager) {
        return new Dragon((String) primitives.get(i++), template.getCoordinates(), (Integer) primitives.get(i++), (String) primitives.get(i), template.getColor(), template.getCharacter(), template.getCave(), manager.dragons);
    }
}
