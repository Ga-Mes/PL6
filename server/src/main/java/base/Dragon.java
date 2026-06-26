package base;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.*;

public class Dragon implements Comparable<Dragon> {

    @JacksonXmlProperty(isAttribute = true)
    private Integer id = 0;

    @JacksonXmlProperty
    private String name;

    @JacksonXmlProperty
    private Coordinates coordinates;

    @JacksonXmlProperty
    private Date creationDate;

    @JacksonXmlProperty
    private Integer age;

    @JacksonXmlProperty
    private String description;

    @JacksonXmlProperty
    private Color color;

    @JacksonXmlProperty
    private DragonCharacter character;

    @JacksonXmlProperty
    private DragonCave cave;

    public Dragon(String name, Coordinates coordinates, Integer age, String description, Color color, DragonCharacter character, DragonCave cave, TreeMap<Integer, Dragon> dragons) {
        this.name = name;
        this.coordinates = coordinates;
        this.age = age;
        this.description = description;
        this.color = color;
        this.character = character;
        this.cave = cave;

        if (dragons.isEmpty()) {
            id = 1;
        } else {
            Set<Integer> range = new HashSet<>();

            for (int i = 1; i <= dragons.size() + 1; i++) {
                range.add(i);
            }

            range.removeAll(dragons.keySet());

            id = Collections.min(range);
        }

        creationDate = new Date();
    }

    public Dragon(Integer id, String name, Coordinates coordinates, Date creationDate, Integer age, String description, Color color, DragonCharacter character, DragonCave cave) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.age = age;
        this.description = description;
        this.color = color;
        this.character = character;
        this.cave = cave;
    }

    public Dragon() {}

    public void exchange(Dragon o) {
        name = o.name;
        coordinates = o.coordinates;
        age = o.age;
        description = o.description;
        color = o.color;
        character = o.character;
        cave = o.cave;
        creationDate = o.creationDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public Integer getAge() {
        return age;
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public DragonCharacter getCharacter() {
        return character;
    }

    public DragonCave getCave() {
        return cave;
    }

    @Override
    public String toString() {
        return "(Dragon)\n" +
                "Id: " + id + ",\n" +
                "Name: " + name + ",\n" +
                "Age: " + age + ",\n" +
                "Creation date: " + creationDate + ",\n" +
                "Description: " + description + ",\n" +
                "Color: " + color + ",\n" +
                "Character: " + character + ",\n" +
                cave + ",\n" +
                coordinates;
    }

    @Override
    public int compareTo(Dragon o) {
        return Integer.compare(this.id, o.id);
    }
}
