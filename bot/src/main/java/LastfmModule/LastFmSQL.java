package LastfmModule;

import java.sql.*;

public class LastFmSQL {
    private Connection conn = null;
    private Statement state = null;


    public LastFmSQL(){
        //try to connect to db
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:LastFm_DB.sqlite");
            System.out.println("Connected to SQLite Database");
            //conn.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("classnotfoundexception");
        } catch (SQLException e) {
            System.out.println("sql exception");
            e.printStackTrace();
        }

    }

    public String[] listUsers(){
        String [] data = new String[4];

        try {

            this.state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT * FROM fmUsers");
            String discordID = "";
            String username = "";
            String toptracks = "";
            String topartists = "";
            while (rs.next()){
                discordID += rs.getString("discordID") + " ";
                username += rs.getString("fmUsername") + " ";
                toptracks += rs.getString("toptracks") + " ";
                topartists += rs.getString("topartists") + " ";


            }
            data[0] = discordID;
            data[1] = username;
            data[2] = toptracks;
            data[3] = topartists;
            //System.out.println(data[0] + " " + data[1] + " " +data[2]+ " " + data[3]);
            System.out.println(data[2]);

        } catch (SQLException e) {

            e.printStackTrace();
            System.out.println("SQL EXCEPTION");
        }
        return data;
    }
    public void openConnnection(){
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:LastFm_DB.sqlite");
            System.out.println("Successfully connected to SQLite Database");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("sql exception");
            e.printStackTrace();
        }
    }

    public void executeQuery(String discordID, String fmUsername, String toptracks, String topartists){
        String updateQuery = "UPDATE fmUsers SET toptracks = '"+toptracks+"', topartists = '"+topartists+"', fmUsername = '"+fmUsername+"' WHERE discordID = '"+discordID+"';";
        String insertQuery = "INSERT INTO fmUsers(discordID, fmUsername, toptracks, topartists) VALUES('"+discordID+"', '"+fmUsername+"', '"+toptracks+"', '"+topartists+"');";
        try {
            if(checkQuery(discordID)){
                //System.out.println("found");
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET toptracks = ?, topartists = ?, fmUsername = ? WHERE discordID = ?;");
                ps.setString(1,toptracks);
                ps.setString(2,topartists);
                ps.setString(3, fmUsername);
                ps.setString(4, discordID);
                ps.executeUpdate();
                //state.executeUpdate(updateQuery);
            }
            else {
                //System.out.println("notfound");
                PreparedStatement ps = conn.prepareStatement("INSERT INTO fmUsers(discordID, fmUsername, toptracks, topartists) VALUES (?, ?, ?, ?);");
                ps.setString(1, discordID);
                ps.setString(2, fmUsername);
                ps.setString(3, toptracks);
                ps.setString(4, topartists);
                ps.executeUpdate();

                //state.executeUpdate(insertQuery);
            }


        } catch (SQLException e) {
            System.out.println("sqlexception");
            e.printStackTrace();
        }

    }
    public void updateUsername(String discordID, String username){
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET fmUsername = ? WHERE discordID = ?;");
            ps.setString(1, username);
            ps.setString(2, discordID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTopTracks(String discordID, String toptracks){
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET toptracks = ? WHERE discordID = ?;");
            ps.setString(1, toptracks);
            ps.setString(1, discordID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void updateTopArtists(String discordID, String topartists){
        try {
            PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET topartists = ? WHERE discordID = ?;");
            ps.setString(1, topartists);
            ps.setString(2, discordID);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public boolean checkQuery(String query) {
        String discordID = "";
        try {
            this.state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT * FROM fmUsers");
            while (rs.next()) {
                discordID += rs.getString("discordID") + " ";
            }
        } catch (SQLException e) {
            System.out.println("discordID not found in database (SQLException)");
            return false;
        }
        //System.out.println(discordID);
        if (!discordID.equals("")){
            return discordID.contains(query);
        }
        else return false;


    }

    public void deleteQuery(String discordID, String username){
        String query = "DELETE FROM fmUsers WHERE fmUsername = '" + username + "' AND discordID = '"+discordID+"';";
        try {
            this.state = conn.createStatement();
            state.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        String test = "test xd \n test xd \n test xd \n test xd \n test xd \n test xd \n test xd \n test xd \n test xd \n test xd \n ";
        LastFmSQL sql = new LastFmSQL();
        sql.checkQuery("110372734118174720");
        //sql.executeQuery("test", "test", test, "test");
        sql.updateUsername("test", "jabba");
        //sql.deleteQuery("test", "test");
        //sql.deleteQuery("110372734118174720", "test");
        sql.listUsers();

        sql.closeConnection();
    }
}
