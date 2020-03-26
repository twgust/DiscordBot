package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopArtistProfileParser {

    private static String[][] resultArtists;

    public LastFmTopArtistProfileParser(String apikey, String username) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettopartists&period=overall&user=" + username + "&limit=1000&api_key=" + apikey + "&format=json")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(LastFmTopArtistProfileParser::parse)
                .join();
    }

    public static void parse(String responsebody) {

        JSONObject artistinfo = new JSONObject(responsebody);
        JSONObject topartist = artistinfo.getJSONObject("topartists");
        JSONArray tracks = topartist.getJSONArray("artist");
        String[][] result = new String[1][4];
        //System.out.println(tracks);


        String artistname = "";
        String playcount = "";
        String artistlink = "";
        String rank = "";
        try {
            JSONObject json = tracks.getJSONObject(0);
            artistname = json.getString("name");
            playcount = json.getString("playcount");
            artistlink = json.getString("url");
            rank = json.getJSONObject("@attr").getString("rank");
        }catch (Exception e){

        }


        result[0][0] = rank;
        result[0][1] = artistname;
        result[0][2] = artistlink;
        result[0][3] = playcount;
        //test if results work


        /*
        System.out.println(result[0][0]);
        System.out.println(result[0][1]);
        System.out.println(result[0][2]);
        System.out.println(result[0][3]);

         */


        resultArtists = result;
    }

    public static String[][] getResultArtists() {
        return resultArtists;
    }

    public static void main(String[] args) {
        LastFmTopArtistProfileParser ta = new LastFmTopArtistProfileParser("c806a80470bbd773b00c2b46b3a1fd75", "robi874");
        System.out.println(Arrays.deepToString(LastFmTopArtistProfileParser.getResultArtists()));
    }

}
