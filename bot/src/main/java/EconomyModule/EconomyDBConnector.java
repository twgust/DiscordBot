package EconomyModule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class EconomyDBConnector {

    public EconomyDBConnector() {
        connect();
        createTable();
    }
    Connection conn = null;
    private void connect() {
        try {
            String url = "jdbc:sqlite:db/economyDB.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void createTable() {
        try {
            System.out.println("Creating DB table if it does not exist.");
            Statement stmt = conn.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS WALLETS " +
                           "(id INTEGER not NULL, " +
                           " total INTEGER not NULL," +
                           " PRIMARY KEY ( id ))";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
