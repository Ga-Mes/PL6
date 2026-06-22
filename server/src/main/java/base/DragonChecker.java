package base;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;

import java.util.ArrayList;
import java.util.Arrays;

public class DragonChecker {
    public static DragonTemplate create(Terminal terminal) {
        LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

        System.out.println("(Coordinates)");

        double x;

        while (true) {
            System.out.print("Enter x: ");

            String line = reader.readLine();

            try {
                x = Double.parseDouble(line);

                if (!(Double.isInfinite(x) || Double.isNaN(x))) {
                    if (x <= 982) {
                        break;
                    }
                }
            } catch (Exception ignored) {}

            System.out.println("Try again... X must be finite and less or equal to 982.");
        }

        double y;

        while (true) {
            System.out.print("Enter y: ");

            String line = reader.readLine();

            try {
                y = Double.parseDouble(line);

                if (!(Double.isInfinite(y) || Double.isNaN(y))) {
                    if (y <= 676) {
                        break;
                    }
                }
            } catch (Exception ignored) {}

            System.out.println("Try again... Y must be finite and less or equal to 676.");
        }

        Coordinates coordinates = new Coordinates(x, y);

        System.out.println("(DragonCave)");

        Long depth;

        while (true) {
            System.out.print("Enter depth: ");

            String line = reader.readLine();

            try {
                if (line.isEmpty()) {
                    depth = null;
                } else {
                    depth = Long.parseLong(line);
                }

                break;
            } catch (Exception ignored) {}

            System.out.println("Try again...");
        }

        int numberOfTreasures;

        while (true) {
            System.out.print("Enter numberOfTreasures: ");

            String line = reader.readLine();

            try {
                numberOfTreasures = Integer.parseInt(line);

                if (numberOfTreasures > 0) {
                    break;
                }
            } catch (Exception ignored) {}

            System.out.println("Try again... Number of treasures must be finite and more than 0.");
        }

        DragonCave dragonCave = new DragonCave(depth, numberOfTreasures);

        System.out.println("(Color)");

        Color color;

        outer:
        while (true) {
            System.out.println(Arrays.toString(Color.values()));

            System.out.print("Enter color: ");

            String line = reader.readLine();

            try {
                if (line.isEmpty()) {
                    color = null;
                    break;
                } else {
                    for (Color possibleColor : Color.values()) {
                        if (possibleColor.toString().equals(line.toUpperCase())) {
                            color = possibleColor;
                            break outer;
                        }
                    }
                }
            } catch (Exception ignored) {}

            System.out.println("Try again... No such color.");
        }

        DragonCharacter dragonCharacter;

        outer:
        while (true) {
            System.out.println(Arrays.toString(DragonCharacter.values()));

            System.out.print("Enter dragonCharacter: ");

            String line = reader.readLine();

            try {
                if (line.isEmpty()) {
                    dragonCharacter = null;
                    break;
                } else {
                    for (DragonCharacter character : DragonCharacter.values()) {
                        if (character.toString().equals(line.toUpperCase())) {
                            dragonCharacter = character;
                            break outer;
                        }
                    }
                }
            } catch (Exception ignored) {}

            System.out.println("Try again... No such character.");
        }

        return new DragonTemplate(coordinates, color, dragonCharacter, dragonCave);
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
