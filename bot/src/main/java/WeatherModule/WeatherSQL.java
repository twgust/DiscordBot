package WeatherModule;

import java.sql.*;

public class WeatherSQL {

    private Connection conn = null;
    private Statement state = null;

    public WeatherSQL(){
        try {
            //Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:db/Weather_DB.db");
            this.state = conn.createStatement();
            System.out.println("Connected to SQLite Database");
            //conn.close();
        } catch (SQLException e) {
            System.out.println("sql exception");
            e.printStackTrace();
            createDB();
        }

    }

    public static void createDB(){
        System.out.println("Trying to create Weather Database");
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:db/Weather_DB.db");
            Statement statement = conn.createStatement();
            statement.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS WEATHER ("+
                            "discordID VARCHAR(255) ,"+
                            "city VARCHAR(255),"+
                            "PRIMARY KEY ( discordID ))");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void setCity (String discordID, String city){
        if(!checkQuery(discordID))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO WEATHER(discordID, city) VALUES (?,?);");
                ps.setString(1, discordID);
                ps.setString(2, city);
                ps.executeUpdate();
            } catch (SQLException e) {
                updateCity(discordID, city);
                //e.printStackTrace();
            }
        }
        else updateCity(discordID, city);

    }

    public void updateCity(String discordID, String city){

        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE WEATHER SET city = ? WHERE discordID = ?;");
            ps.setString(1, city);
            ps.setString(2, discordID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkQuery(String query) {
        String discordID = "";
        String city = "";
        try {
            this.state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT city,discordID FROM WEATHER WHERE discordID = '"+query+"';");
            while (rs.next()) {
                discordID = rs.getString("discordID");
                city = rs.getString("city");
                //System.out.println(discordID);
            }
        } catch (SQLException e) {
            System.out.println("discordID not found in database (SQLException)");
            return false;
        }
        //System.out.println(discordID);
        //System.out.println(discordID.contains(query));
        if(discordID.equals("")){
            return false;
        }
        else return city != null;

    }

    public String getCity(String discordID){
        String city = "";
        try {
            this.state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT city FROM WEATHER WHERE discordID ='"+discordID+"';");
            city = rs.getString("city");
            System.out.println(city);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return city;
    }

    public void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("sql exception");
            e.printStackTrace();
        }
    }
}
