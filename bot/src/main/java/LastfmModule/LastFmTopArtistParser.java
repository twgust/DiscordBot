package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopArtistParser {

    private String [][] resultArtists;
    private int limit = 1000;

    //changed thenAccpet (CLASSNAME::parse) to current to not make it static
    public LastFmTopArtistParser(String apikey, String username, String period){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettopartists&period="+period+"&user="+username+"&limit="+limit+"&api_key="+apikey+"&format=json")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(this::parse)
                .join();
    }

    public void parse(String responsebody) {

        JSONObject artistinfo = new JSONObject(responsebody);
        JSONObject topartist = artistinfo.getJSONObject("topartists");
        JSONArray tracks = topartist.getJSONArray("artist");
        String[][] result = new String[tracks.length()][4];
        //System.out.println(tracks);

        for (int i = 0; i < tracks.length(); i++) {
            JSONObject json = tracks.getJSONObject(i);
            String artistname = json.getString("name");
            String playcount = json.getString("playcount");
            String artistlink = json.getString("url");
            String rank = json.getJSONObject("@attr").getString("rank");

            result[i][0] = rank;
            result[i][1] = artistname;
            result[i][2] = artistlink;
            result[i][3] = playcount;
            //test if results work

            /*
            System.out.println(result[i][0]);
            System.out.println(result[i][1]);
            System.out.println(result[i][2]);
            System.out.println(result[i][3]);

             */


        }
        resultArtists = result;
    }

    public String[][] getResultArtists() {
        return resultArtists;
    }

    public static void main(String[] args) {
        LastFmTopArtistParser ta = new LastFmTopArtistParser("c806a80470bbd773b00c2b46b3a1fd75", "robi874", "overall");
        System.out.println(Arrays.deepToString(ta.getResultArtists()));
        LastFmTopArtistParser ta2 = new LastFmTopArtistParser("c806a80470bbd773b00c2b46b3a1fd75", "abhi_sama", "overall");
        System.out.println(Arrays.deepToString(ta2.getResultArtists()));
        System.out.println();
    }

}
