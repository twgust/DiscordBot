package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopTracksProfileParser {

    private static String[][] resultTracks;

    public LastFmTopTracksProfileParser(String apikey, String username) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&period=overall&user=" + username + "&limit=1000&api_key=" + apikey + "&format=json")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(LastFmTopTracksProfileParser::parse)
                .join();
    }


    public static void parse(String responsebody) {

        JSONObject trackinfo = new JSONObject(responsebody);
        JSONObject toptracks = trackinfo.getJSONObject("toptracks");
        JSONArray tracks = toptracks.getJSONArray("track");
        String[][] result = new String[tracks.length()][5];
        //System.out.println(tracks);


        JSONObject json = tracks.getJSONObject(0);
        String artistname = json.getJSONObject("artist").getString("name");
        String trackname = json.getString("name");
        String playcount = json.getString("playcount");
        String tracklink = json.getString("url");
        String rank = json.getJSONObject("@attr").getString("rank");

        result[0][0] = rank;
        result[0][1] = artistname;
        result[0][2] = trackname;
        result[0][3] = tracklink;
        result[0][4] = playcount;
        //test if results work

        /*
        System.out.println(result[0][0]);
        System.out.println(result[0][1]);
        System.out.println(result[0][2]);
        System.out.println(result[0][3]);
        System.out.println(result[0][4]);

         */


        resultTracks = result;
    }

    public static String[][] getResultTracks() {
        return resultTracks;
    }


    public static void main(String[] args) {
        LastFmTopTracksProfileParser tt2 = new LastFmTopTracksProfileParser("c806a80470bbd773b00c2b46b3a1fd75", "abhi_sama");
        //System.out.println(Arrays.deepToString(LastFmTopTracksProfileParser.getResultTracks()));
        LastFmTopTracksProfileParser tt = new LastFmTopTracksProfileParser("c806a80470bbd773b00c2b46b3a1fd75", "robi874");
        //System.out.println(Arrays.deepToString(LastFmTopTracksProfileParser.getResultTracks()));

    }
}
