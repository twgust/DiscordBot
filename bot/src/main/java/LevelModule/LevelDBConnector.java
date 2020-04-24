package LevelModule;

import java.sql.*;

public class LevelDBConnector {
    private Connection conn;
    public LevelDBConnector() {
        connect();
    }

    public void newGuildTable(String guildID){
        createDBTable(guildID);
    }

    public void addUserExp(String guildID, long memberID){
        createDBUser(guildID, memberID);
    }

    private void connect() {
        try {
            String url = "jdbc:sqlite:db/levelDB.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite (levelDB) has been established.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDBTable(String guildID) {
        try {
            System.out.println("Creating levelDB table if it does not exist.");
            Statement stmt = conn.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS "+ guildID +
                    " (id INTEGER not NULL, " +
                    " currentExp INTEGER not NULL," +
                    " nextLevelExp INTEGER not NULL," +
                    " level INTEGER not NULL," +
                    " PRIMARY KEY ( id ))";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createDBUser(String guildID, long memberID){
        createDBTable(guildID);
        try {
            Statement stmt = conn.createStatement();
            String query = "INSERT INTO " + guildID + "(id,currentExp,nextLevelExp,level)" +
                    "VALUES(" + memberID + "," + 0 + "," + 20 + ","+ 0 +")";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addExpToDBUser(String guildID, long memberID){
        try{
            Statement stmt = conn.createStatement();
            String query = "SELECT currentExp FROM "+guildID+" WHERE id=" + memberID;
        }catch (SQLException e){

        }
    }

    private boolean DBUserExist(String guildID, long memberID){
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM "+guildID +" WHERE  id=" + memberID;
            ResultSet rs = stmt.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            return false;
        }
    }
}
