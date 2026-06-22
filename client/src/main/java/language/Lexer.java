package language;

import command.CommandType;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

public class Lexer {
    private static final Map<CommandType, Type[]> defaultArgs = Map.ofEntries(
            Map.entry(CommandType.HELP, new Type[]{}),
            Map.entry(CommandType.INFO, new Type[]{}),
            Map.entry(CommandType.SHOW, new Type[]{}),
            Map.entry(CommandType.INSERT, new Type[]{Integer.class, String.class, Integer.class, String.class}),
            Map.entry(CommandType.UPDATE, new Type[]{Integer.class, String.class, Integer.class, String.class}),
            Map.entry(CommandType.REMOVE_KEY, new Type[]{Integer.class}),
            Map.entry(CommandType.CLEAR, new Type[]{}),
            Map.entry(CommandType.EXIT, new Type[]{}),
            Map.entry(CommandType.REMOVE_GREATER, new Type[]{String.class, Integer.class, String.class}),
            Map.entry(CommandType.REMOVE_GREATER_KEY, new Type[]{Integer.class}),
            Map.entry(CommandType.FILTER_CONTAINS_DESCRIPTION, new Type[]{String.class}),
            Map.entry(CommandType.FILTER_GREATER_THAN_AGE, new Type[]{Integer.class}),
            Map.entry(CommandType.PRINT_UNIQUE_COLOR, new Type[]{}),
            Map.entry(CommandType.PORT, new Type[]{Integer.class})
    );

    private static ArrayList<String> split(String text) {
        text = " " + text + " ";

        ArrayList<Integer> qI = new ArrayList<>();

        ArrayList<int[]> qR = new ArrayList<>();

        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '"') {
                if (qI.isEmpty()) {
                    qI.add(i);
                } else {
                    qR.add(new int[]{qI.get(qI.size() - 1), i});
                    qI.remove(qI.size() - 1);
                }
            }
        }

        if (!qI.isEmpty()) {
            System.out.println("Wrong quotation in line...");

            return null;
        }

        return getTokens(text, qR);
    }

    private static ArrayList<String> getTokens(String text, ArrayList<int[]> qR) {
        ArrayList<Integer> spaces = new ArrayList<>();

        outer:
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == ' ') {
                for (int[] range : qR) {
                    if ((range[0] < i) && (i < range[1])) {
                        continue outer;
                    }
                }

                spaces.add(i);
            }
        }

        ArrayList<String> tokens = new ArrayList<>();

        for (int i = 1; i < spaces.size(); i++) {
            String sLine = text.substring(spaces.get(i - 1) + 1, spaces.get(i));

            if (!sLine.isBlank()) {
                tokens.add(sLine);
            }
        }

        return tokens;
    }

    public static ArrayList<Object> compile(String input) {
        ArrayList<String> tokens = split(input);

        if (tokens == null) {
            return null;
        }

        try {
            CommandType commandType = CommandType.valueOf(tokens.get(0).toUpperCase());

            tokens.remove(0);

            if (defaultArgs.get(commandType).length != tokens.size()) {
                System.out.println("Wrong number of arguments: expected " + defaultArgs.get(commandType).length + ", but " + tokens.size() + " given...");

                return null;
            }

            ArrayList<Object> result = new ArrayList<>();

            result.add(commandType);

            for (int i = 0; i < tokens.size(); i++) {
                Type eType = defaultArgs.get(commandType)[i];

                Object arg = tokens.get(i);

                if (eType == Integer.class) {
                    try {
                        arg = Integer.parseInt(tokens.get(i));
                    } catch (NumberFormatException e) {
                        System.out.println(tokens.get(i) + " <- argument has wrong type. Expected integer...");

                        return null;
                    }
                }

                result.add(arg);
            }

            return result;
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong command name...");

            return null;
        }
    }
}
