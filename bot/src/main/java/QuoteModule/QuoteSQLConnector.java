package QuoteModule;

import java.sql.*;

public class QuoteSQLConnector {
    private Connection conn = null;

    public QuoteSQLConnector() {
        connect();
        createTable();
    }

    private void connect() {
        try {
            String url = "jdbc:sqlite:db/quoteDB.db";
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try {
            Statement statement = conn.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS QUOTES " +
                    "(id INTEGER PRIMARY KEY AUTOINCREMENT not NULL, " +
                    " userId VARCHAR(255) not NULL, " +
                    " quote TINYTEXT not NULL)";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addQuote(String userID, String quote) {
        try {
            Statement stmt = conn.createStatement();
            String query = "INSERT INTO QUOTES (userId,quote)" +
                    "VALUES(" + userID + "," + quote + ")";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
