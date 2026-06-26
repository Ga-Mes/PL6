package data;

import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseManager {
    private final String url = "jdbc:postgresql://localhost:5432/studs";

    private final String login = "s505345";

    private final String secret = "70ddjuMWzRju6JfQ";

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
            PreparedStatement pS = connection.prepareStatement("CREATE TABLE IF NOT EXISTS users (login VARCHAR(255) PRIMARY KEY, password_hash CHAR(64) NOT NULL); CREATE TABLE IF NOT EXISTS dragons (c_id BIGSERIAL PRIMARY KEY, id INTEGER NOT NULL, owner_login VARCHAR(255) NOT NULL REFERENCES users(login), name VARCHAR(255) NOT NULL, coordinates JSONB, creation_date TIMESTAMP NOT NULL, age INTEGER, description TEXT, color VARCHAR(50), character VARCHAR(50), cave JSONB);");

            pS.execute();
        } catch (SQLException e) {
            logger.error("Couldn't create all tables. Program will abort...");
        }
    }

    private boolean connect() {
        try {
            connection = DriverManager.getConnection(url, login, secret);
        } catch (SQLException e) {
            return false;
        }

        return true;
    }
}
