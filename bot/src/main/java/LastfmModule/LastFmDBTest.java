package LastfmModule;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LastFmDBTest {
    public static void main(String[] args) {
        LastFmDB test = new LastFmDB();
        ResultSet rs;

        try {
            rs = test.displayUsers();

            while (rs.next()){
                System.out.println(rs.getString("fname") + " " + rs.getString("lname"));

            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
