package fr.framelab;

import java.sql.*;

public class DataBaseManager {

    private static Connection connection;


    public static Connection getConnexion() throws SQLException {
        if (connection == null) {
            connection = DriverManager.getConnection("jdbc:sqlite:FrameLab.db");
            Statement stmt = connection.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");
            initializeTable();
        }
        return connection;

    }

    private static void initializeTable () throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS projects (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                challengeId INTEGER NOT NULL
                )
                """;
        try (PreparedStatement pstmt =connection.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Table creation failed", e);
        }
    }

}
