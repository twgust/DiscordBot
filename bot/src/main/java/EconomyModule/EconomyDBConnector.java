package EconomyModule;

import java.sql.*;

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

    public void createUser(int id, int initialValue) {
        try {
            Statement stmt = conn.createStatement();
            String query = "INSERT INTO WALLETS (id,total)" +
                           "VALUES(" + id + "," + initialValue + ")";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean userExists(int id) {
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM WALLETS WHERE  id=" + id;
            ResultSet rs = stmt.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            return false;
        }
    }
    public int getRowTotal(int id) {
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT total FROM WALLETS WHERE id=" + id;
            ResultSet rs = stmt.executeQuery(query);
            return rs.getInt("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
