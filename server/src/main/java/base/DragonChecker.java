package base;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.Arrays;

public class DragonChecker {
    public Dragon form(DragonTemplate template) {

    }

    public static boolean check(ArrayList<Object> primitives, int i) {
        if (primitives.size() - i != 3) return false;

        String name = (String) primitives.get(i++);

        if (name.isBlank()) {
            return false;
        }

        int age = Integer.parseInt((String) primitives.get(i));

        return age >= 1;
    }
}
