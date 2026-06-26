package data;

import base.*;
import com.fasterxml.jackson.core.JacksonException;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HexFormat;
import java.util.TreeMap;

public class DatabaseManager {
    private final String url = "jdbc:postgresql://localhost:5432/studs";

    private final String bUser = "s505345";

    private final String bSecret = "70ddjuMWzRju6JfQ";

    private Connection connection;

    private final Logger logger;

    public DatabaseManager(Logger logger) throws Exception {
        this.logger = logger;

        if (!connect()) {
            logger.error("Couldn't connect to the database. Program will abort...");

            throw new Exception();
        }

        logger.info("Connected to the database!");

        try {
            PreparedStatement pS = connection.prepareStatement("CREATE TABLE IF NOT EXISTS USERS (login TEXT PRIMARY KEY, password_hash CHAR(64) NOT NULL); CREATE TABLE IF NOT EXISTS DRAGON (c_id INTEGER PRIMARY KEY, id SERIAL UNIQUE NOT NULL, owner_login TEXT NOT NULL REFERENCES USERS(login), name TEXT, coordinates XML, creation_date TIMESTAMP NOT NULL, age INTEGER, description TEXT, color TEXT, character TEXT, cave XML);");

            pS.execute();
        } catch (SQLException e) {
            logger.error("Couldn't create all tables. Program will abort...");
        }
    }

    public boolean register(String login, String password) {
        try {
            PreparedStatement pS = connection.prepareStatement("INSERT INTO USERS(login, password_hash) VALUES (?, ?)");

            pS.setString(1, login);
            pS.setString(2, sha256(password));

            pS.executeUpdate();

            return true;
        } catch (SQLException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    private String sha256(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));

        return HexFormat.of().formatHex(hash);
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException ignored) {}
    }

    public boolean authorize(String login, String password) {
        String sql = "SELECT password_hash FROM USERS WHERE login = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                String storedHash = rs.getString("password_hash");
                String enteredHash = sha256(password);

                return storedHash.equals(enteredHash);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    public boolean clear(TreeMap<Integer, Dragon> dragons, String login) {
        try {
            PreparedStatement ps = connection.prepareStatement(
                    "DELETE FROM DRAGON WHERE owner_login = ? RETURNING c_id"
            );

            ps.setString(1, login);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int cId = rs.getInt("c_id");

                    dragons.remove(cId);
                }
            }

            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean removeKey(TreeMap<Integer, Dragon> dragons, String login, Integer key) {
        String sql = """
            DELETE FROM DRAGON
            WHERE owner_login = ? AND c_id = ?
            RETURNING c_id
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setInt(2, key);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                int cId = rs.getInt("c_id");
                dragons.remove(cId);

                return true;
            }

        } catch (SQLException e) {
            return false;
        }
    }

    public boolean insert(Integer key, Dragon dragon, String login, TreeMap<Integer, Dragon> dragons) {
        String sql = """
            INSERT INTO DRAGON (
                c_id,
                owner_login,
                name,
                coordinates,
                creation_date,
                age,
                description,
                color,
                character,
                cave
            )
            VALUES (?, ?, ?, ?::xml, ?, ?, ?, ?, ?, ?::xml)
            RETURNING id
            """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, key);
            ps.setString(2, login);
            ps.setString(3, dragon.getName());

            ps.setString(
                    4,
                    dragon.getCoordinates() == null
                            ? null
                            : XMLWorker.serialize(dragon.getCoordinates())
            );

            ps.setTimestamp(5, new Timestamp(dragon.getCreationDate().getTime()));
            ps.setObject(6, dragon.getAge());
            ps.setString(7, dragon.getDescription());

            ps.setString(
                    8,
                    dragon.getColor() == null
                            ? null
                            : dragon.getColor().name()
            );

            ps.setString(
                    9,
                    dragon.getCharacter() == null
                            ? null
                            : dragon.getCharacter().name()
            );

            ps.setString(
                    10,
                    dragon.getCave() == null
                            ? null
                            : XMLWorker.serialize(dragon.getCave())
            );

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return false;
                }

                dragon.setId(rs.getInt("id"));
            }

            dragons.put(key, dragon);

            return true;

        } catch (SQLException | JacksonException e) {
            logger.error(e.getMessage());

            return false;
        }
    }

    public TreeMap<Integer, Dragon> load() {
        TreeMap<Integer, Dragon> result = new TreeMap<>();

        String sql = "SELECT * FROM DRAGON ORDER BY c_id";

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int cId = rs.getInt("c_id");

                int id = rs.getInt("id");
                String name = rs.getString("name");

                String coordinatesXml = rs.getString("coordinates");
                Coordinates coordinates = coordinatesXml == null
                        ? null
                        : XMLWorker.parse(coordinatesXml, Coordinates.class);

                String caveXml = rs.getString("cave");
                DragonCave cave = caveXml == null
                        ? null
                        : XMLWorker.parse(caveXml, DragonCave.class);

                Date creationDate = new Date(rs.getTimestamp("creation_date").getTime());

                Integer age = (Integer) rs.getObject("age");

                String description = rs.getString("description");

                String colorStr = rs.getString("color");
                Color color = colorStr == null
                        ? null
                        : Color.valueOf(colorStr);

                String characterStr = rs.getString("character");
                DragonCharacter character = characterStr == null
                        ? null
                        : DragonCharacter.valueOf(characterStr);

                result.put(
                        cId,
                        new Dragon(
                                id,
                                name,
                                coordinates,
                                creationDate,
                                age,
                                description,
                                color,
                                character,
                                cave
                        )
                );
            }

        } catch (SQLException | JacksonException e) {
            logger.error("Couldn't load the collection. It will be empty...", e);

            return new TreeMap<>();
        }

        return result;
    }

    private boolean connect() {
        try {
            connection = DriverManager.getConnection(url, bUser, bSecret);
        } catch (SQLException e) {
            return false;
        }

        return true;
    }
}
