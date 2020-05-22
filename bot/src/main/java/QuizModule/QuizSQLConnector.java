package QuizModule;

import net.dv8tion.jda.api.entities.User;

import java.sql.*;
import java.util.AbstractMap;
import java.util.ArrayList;

/**
 * QuizSQLConnector is the connecting class between the Quiz Module and its corresponding data table. The table will
 * contain user points
 * @author Carl Johan Helgstrand
 * @version 1.0
 */
public class QuizSQLConnector {
    private Connection conn = null;

    public QuizSQLConnector() {
        connect();
        createTable();
    }

    /**
     * Setting up a connection with the DB table. The connecting is maintained as long as the Bot is running
     */
    private void connect() {
        try {
            String url = "jdbc:sqlite:db/quizDB.db";
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new table if none exists in the database
     */
    private void createTable() {
        try {
            Statement statement = conn.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS POINTS " +
                    "(id VARCHAR(255) not NULL, " +
                    " points INTEGER(100000) not NULL," +
                    " PRIMARY KEY ( id ))";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new row in the database for a new user. User is only added when they gain their first points
     * @param id Discord user Id
     * @param initialPoints Users starting points
     */
    private void createUser(String id, int initialPoints) {
        try {
            Statement stmt = conn.createStatement();
            String query = "INSERT INTO POINTS (id,points)" +
                    "VALUES(" + id + "," + initialPoints + ")";
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a user exists in the DB table
     * @param id Discord user Id
     * @return True or false
     */
    private boolean userExists(String id) {
        try {
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM POINTS WHERE  id=" + id;
            return stmt.executeQuery(query).next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Adds points to an existing user
     * @param id Discord User Id
     * @param points Users points to be added
     */
    public void addToPoints(String id, int points) {
        if (!userExists(id)) {
            createUser(id, 0);
        }
        try {
            Statement stmt = conn.createStatement();
            String query = "UPDATE POINTS " +
                    " SET points = points + " + points + " " +
                    " WHERE id = " + id;
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the users points
     * @param id Discord user Id
     * @return User points
     */
    public int getPoints(String id) {
        try {
            Statement statement = conn.createStatement();
            String query = "SELECT points FROM POINTS WHERE id=" + id;
            return statement.executeQuery(query).getInt("points");
        } catch (SQLException e) {
            // e.printStackTrace();
            return 0;

        }
    }

    /**
     * Returns the user with the highest score
     * @return Entry with Discord User Id and User points
     */
    public AbstractMap.SimpleEntry<String, Integer> getHighestScore() {
        AbstractMap.SimpleEntry<String, Integer> bestScoreUser = new AbstractMap.SimpleEntry<String, Integer>("-1", 0); //Temp
        try {
            Statement statement = conn.createStatement();
            String query = "SELECT id, COUNT(*) FROM POINTS GROUP BY id";
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()) {
                if (rs.getInt(2) > bestScoreUser.getValue()) {
                    bestScoreUser = new AbstractMap.SimpleEntry<String, Integer>(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException e) {
            // e.printStackTrace();
        }
                return bestScoreUser;
    }

}
