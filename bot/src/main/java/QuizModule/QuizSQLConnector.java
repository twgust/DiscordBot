package QuizModule;

import java.sql.*;

public class QuizSQLConnector {
    private Connection conn = null;

    public QuizSQLConnector() {
        connect();
        createTable();
    }

    private void connect() {
        try {
            String url = "jdbc:sqlite:db/quizDB.db";
            conn = DriverManager.getConnection(url);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try {
            Statement statement = conn.createStatement();
            String query = "CREATE TABLE IF NOT EXISTS POINTS " +
                            "(id VARCHAR(255) not NULL, " +
                            " points INTEGER not NULL," +
                            " PRIMARY KEY ( id ))";
            statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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

    public void addToPoints(String id, int points) {
        if(!userExists(id)){
           createUser(id, points);
        }
        try {
            Statement stmt = conn.createStatement();
            String query ="UPDATE POINTS " +
                    " SET points = points + " + points + " " +
                    " WHERE id = " + id;
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getPoints(String id) {
        try {
            Statement statement = conn.createStatement();
            String query = "SELECT points FROM POINTS WHERE id=" + id;
            return statement.executeQuery(query).getInt("points");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; //Database error
    }

}
