package LevelModule;

import net.dv8tion.jda.api.entities.TextChannel;

import java.sql.*;

public class LevelDBConnector {
    private Connection conn;
    public LevelDBConnector() {
        connect();
    }

    public void newGuildTable(String guildID){
        createDBTable(guildID);
    }

    public void addUserExp(String guildID, long memberID, TextChannel channel){
        addExpToDBUser(guildID, memberID, channel);
    }

    public boolean userExist(String guildID, long memberID){
        return DBUserExist(guildID, memberID);
    }

    public String[] getUserInfo(String guildID, long memberID){
        return getDBUserInfo(guildID, memberID);
    }

    public void createUser(String guildID, long memberID){
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
        try {
            Statement stmt = conn.createStatement();
            String query = "INSERT INTO " + guildID + "(id,currentExp,nextLevelExp,level)" +
                    "VALUES(" + memberID + "," + 0 + "," + 20 + ","+ 0 +")";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addExpToDBUser(String guildID, long memberID, TextChannel channel){
        try{
            int tempExp = 0;
            int nextLevelExp = 0;
            Statement stmt = conn.createStatement();
            String expQuery = "SELECT currentExp FROM "+guildID+" WHERE id= " + memberID;
            tempExp = stmt.executeQuery(expQuery).getInt("currentExp") + 1;
            String lvlQuery = "SELECT nextLevelExp FROM "+guildID+" WHERE id= " + memberID;
            nextLevelExp = stmt.executeQuery(lvlQuery).getInt("nextLevelExp");
            if(tempExp >= nextLevelExp){
                tempExp = 0;
                lvlQuery = "UPDATE " + guildID + " SET level = level + " + 1 + " WHERE id = " + memberID;
                stmt.executeUpdate(lvlQuery);
                lvlQuery = "SELECT level FROM " + guildID + " WHERE id= " + memberID;
                int level = stmt.executeQuery(lvlQuery).getInt("level");
                lvlQuery = "UPDATE " + guildID + " SET nextLevelExp = " + (nextLevelExp + ((level) * 20)) + " WHERE id = " + memberID;
                stmt.executeUpdate(lvlQuery);
                LevelController.levelUP(memberID, level, channel);
            }
            expQuery = "UPDATE " + guildID + " SET currentExp = " + tempExp + " WHERE id = " + memberID;
            stmt.executeUpdate(expQuery);
        }catch (SQLException e){
            System.out.println("addExp sql went bad :(");
            e.printStackTrace();
        }
    }

    private boolean DBUserExist(String guildID, long memberID){
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM "+ guildID +" WHERE  id=" + memberID;
            return stmt.executeQuery(query).next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String[] getDBUserInfo(String guildID, long memberID){
        String[] info = new String[3];
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM "+guildID +" WHERE  id=" + memberID;
            info[0] = Integer.toString(stmt.executeQuery(query).getInt("currentExp"));
            info[1] = Integer.toString(stmt.executeQuery(query).getInt("nextLevelExp"));
            info[2] = Integer.toString(stmt.executeQuery(query).getInt("level"));
            return info;
        } catch (Exception e) {
            e.printStackTrace();
            return info;
        }
    }

}
