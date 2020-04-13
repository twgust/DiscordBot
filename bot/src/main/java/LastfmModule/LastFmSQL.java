package LastfmModule;

import de.umass.lastfm.Period;

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

    public String[] listUser(String period, String username){
        String trackPeriod = "";
        String artistPeriod = "";
        if (period.equalsIgnoreCase("week"))
        {
            trackPeriod = "toptracksWeek";
            artistPeriod = "topartistsWeek";
        }
        else if (period.equalsIgnoreCase("1month"))
        {
            trackPeriod = "toptracks1Month";
            artistPeriod = "topartists1Month";
        }
        else if (period.equalsIgnoreCase("month"))
        {
            trackPeriod = "toptracks3Month";
            artistPeriod = "topartists3Month";
        }
        else if (period.equalsIgnoreCase("6month"))
        {
            trackPeriod = "toptracks6Month";
            artistPeriod = "topartists6Month";
        }
        else if (period.equalsIgnoreCase("12month"))
        {
            trackPeriod = "toptracks12Month";
            artistPeriod = "topartists12Month";
        }
        else if (period.equalsIgnoreCase("overall"))
        {
            trackPeriod = "toptracksOverall";
            artistPeriod = "topartistsOverall";
        }
        else {
            trackPeriod = "toptracksWeek";
            artistPeriod = "topartistsWeek";
        }

        String [] data = new String[4];

        try {

            this.state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT "+trackPeriod+", "+artistPeriod+" FROM fmUsers WHERE fmUsername = '"+username+"';");
            String discordID = "";
            String usernametemp = "";
            String toptracks = "";
            String topartists = "";
            while (rs.next()){
                //discordID += rs.getString("discordID") + " ";
                //usernametemp += rs.getString("fmUsername") + " ";
                toptracks += rs.getString(trackPeriod) + " ";
                topartists += rs.getString(artistPeriod) + " ";


            }
            //data[0] = discordID;
            //data[1] = usernametemp;
            data[2] = toptracks;
            data[3] = topartists;
            //System.out.println(data[0] + " " + data[1] + " " +data[2]+ " " + data[3]);
            //System.out.println(data[2]);
            //System.out.println(data[3]);

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

    //ANVÄNDS ENDAST OM ANVÄNDAREN INTE FINNS I SYSTEMET
    public void setUsername (String discordID, String username){
        if(!checkQuery(discordID))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("INSERT INTO fmUsers(discordID, fmUsername) VALUES (?,?);");
                ps.setString(1, discordID);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                updateUsername(discordID, username);
                //e.printStackTrace();
            }
        }
        else updateUsername(discordID, username);

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

    /*
    public void updateTopTracks(String username, String toptracks, String period){

        if (period.equalsIgnoreCase("7day") || period.equalsIgnoreCase("week"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET toptracksWeek = ? WHERE fmUsername = ?;");
                ps.setString(1, toptracks);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (period.equalsIgnoreCase("1month"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET toptracks1Month = ? WHERE fmUsername = ?;");
                ps.setString(1, toptracks);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(period.equalsIgnoreCase("3month"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET toptracks3Month = ? WHERE fmUsername = ?;");
                ps.setString(1, toptracks);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(period.equalsIgnoreCase("6month"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET toptracks6Month = ? WHERE fmUsername = ?;");
                ps.setString(1, toptracks);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (period.equalsIgnoreCase("12month"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET toptracks12Month = ? WHERE fmUsername = ?;");
                ps.setString(1, toptracks);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (period.equalsIgnoreCase("overall"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET toptracksOverall = ? WHERE fmUsername = ?;");
                ps.setString(1, toptracks);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


    }

     */
/*
    public void updateTopArtists(String username, String topartists, String period){
        if (period.equalsIgnoreCase("week"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET topartistsWeek = ? WHERE fmUsername = ?;");
                ps.setString(1, topartists);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (period.equalsIgnoreCase("1month"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET topartists1Month = ? WHERE fmUsername = ?;");
                ps.setString(1, topartists);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(period.equalsIgnoreCase("3month"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET topartists3Month = ? WHERE fmUsername = ?;");
                ps.setString(1, topartists);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(period.equalsIgnoreCase("6month"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET topartists6Month = ? WHERE fmUsername = ?;");
                ps.setString(1, topartists);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (period.equalsIgnoreCase("12month"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET topartists12Month = ? WHERE fmUsername = ?;");
                ps.setString(1, topartists);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if (period.equalsIgnoreCase("overall"))
        {
            try {
                PreparedStatement ps = conn.prepareStatement("UPDATE fmUsers SET topartistsOverall = ? WHERE fmUsername = ?;");
                ps.setString(1, topartists);
                ps.setString(2, username);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

 */


    public boolean checkQuery(String query) {
        String discordID = "";
        String usernameDB = "";
        try {
            this.state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT fmUsername,discordID FROM fmUsers WHERE discordID = '"+query+"';");
            while (rs.next()) {
                discordID = rs.getString("discordID");
                usernameDB = rs.getString("fmUsername");
                //System.out.println(discordID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("discordID not found in database (SQLException)");
            return false;
        }
        //System.out.println(discordID);
        //System.out.println(discordID.contains(query));
        if(discordID.equals("")){
            return false;
        }
        else return usernameDB != null;

    }

    public String getUsername(String discordID){
        String username = "";
        try {
            this.state = conn.createStatement();
            ResultSet rs = state.executeQuery("SELECT fmUsername FROM fmUsers WHERE discordID ='"+discordID+"';");
            username = rs.getString("fmUsername");
            System.out.println(username);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }

    public void deleteQuery(String discordID){
        String query = "UPDATE fmUsers SET fmUsername = "+null+" WHERE discordID = '"+discordID+"';";
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
        //sql.setUsername("110372734118174720", " robi874");
        sql.checkQuery("110372734118174720");
        sql.deleteQuery("110372734118174720");
        String test1 = "dab";
        //sql.getUsername("110372734118174720");
        //sql.getUsername("110372734118174720");
        System.out.println();

        sql.closeConnection();
    }
}
