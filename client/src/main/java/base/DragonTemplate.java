package base;

public class DragonTemplate {

    private Coordinates coordinates;

    private Color color;

    private DragonCharacter character;

    private DragonCave cave;

    public DragonTemplate(Coordinates coordinates, Color color, DragonCharacter character, DragonCave cave) {
        this.coordinates = coordinates;
        this.color = color;
        this.character = character;
        this.cave = cave;
    }

    public DragonTemplate() {}

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public DragonCharacter getCharacter() {
        return character;
    }

    public void setCharacter(DragonCharacter character) {
        this.character = character;
    }

    public DragonCave getCave() {
        return cave;
    }

    public void setCave(DragonCave cave) {
        this.cave = cave;
    }
}
