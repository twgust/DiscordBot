package LastfmModule;

import java.sql.*;
import java.text.NumberFormat;

public class LastFmDB {

    private static Connection con;
    private static boolean hasData = false;

    public ResultSet displayUsers() throws SQLException, ClassNotFoundException {
        if(con ==null){
            getConnection();
        }

        Statement state = con.createStatement();
        ResultSet res = state.executeQuery("SELECT fname, lname FROM user");
        return res;
    }

    private void getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:LastFm.db");
        initialize();
    }

    private void initialize() throws SQLException {
        if(!hasData){
            hasData = true;
            Statement state = con.createStatement();
            ResultSet res = state.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='user'");
            if( !res.next()) {
                System.out.println("Building the user table with prepopulated values");
                //need to build the table
                Statement state2 = con.createStatement();
                state2.execute("CREATE TABLE user(id integer," + "fName varchar(60)," + "lName varchar (60),"
                        + "primary key(id));");

                // inserting some sample data
                PreparedStatement prep = con.prepareStatement("INSERT INTO user values(?,?,?);");
                prep.setString(2, "John");
                prep.setString(3, "McNeil");
                prep.execute();

                PreparedStatement prep2 = con.prepareStatement("INSERT INTO user values(?,?,?);");
                prep2.setString(2, "Paul");
                prep2.setString(3, "Smith");
                prep2.execute();
            }
        }

    }

    public void addUser(String firstname, String lastname) throws SQLException, ClassNotFoundException {
        if(con == null){
            getConnection();
        }

        PreparedStatement prep = con.prepareStatement("INSERT INTO name user values(?,?,?);");
        prep.setString(2, firstname);
        prep.setString(3, lastname);
        prep.execute();
    }
}
