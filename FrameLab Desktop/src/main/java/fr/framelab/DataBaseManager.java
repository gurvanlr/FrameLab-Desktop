package fr.framelab;

import java.sql.*;

public class DataBaseManager {

    private static Connection connection;


    public static Connection getConnexion() throws SQLException {
        if (connection != null) {
            connection = DriverManager.getConnection("jdbc:sqlite:FrameLab.db");
            Statement stmt = connection.createStatement();
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private void initializeTable () throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXIST projects (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                image TEXT NOT NULL
                )
                """;
        try (PreparedStatement pstmt =connection.prepareStatement(sql)) {
            pstmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Table creation failed", e);
        }
    }

}
