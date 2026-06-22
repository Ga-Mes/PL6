package base;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class DragonWrapper {
    public List<DragonEntry> collection = new ArrayList<>();

    public DragonWrapper() {}

    public DragonWrapper(TreeMap<Integer, Dragon> map) {
        map.forEach((k, v) -> collection.add(new DragonEntry(k, v)));
    }

    public TreeMap<Integer, Dragon> toTreeMap() {
        TreeMap<Integer, Dragon> map = new TreeMap<>();

        for (DragonEntry entry : collection) {
            map.put(entry.key, entry.value);
        }

        return map;
    }
}
