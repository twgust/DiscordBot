package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopTracksParserTEST {

    private static String [][] resultTracks;
    private static int trackamount;

    public LastFmTopTracksParserTEST(String apikey, String username, String period, int trackamount) {
        LastFmTopTracksParserTEST.trackamount = trackamount;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&period="+period+"&user="+username+"&limit=1000&api_key="+apikey+"&format=json")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(LastFmTopTracksParserTEST::parse)
                .join();
    }


    public static void parse(String responsebody) {


        JSONObject trackinfo = new JSONObject(responsebody);
        JSONObject toptracks = trackinfo.getJSONObject("toptracks");
        JSONArray tracks = toptracks.getJSONArray("track");
        String[][] result = new String[tracks.length()][5];
        //System.out.println(tracks);
        if(result.length < trackamount){
            trackamount = result.length;
        }

        for (int i = 0; i < trackamount; i++) {
            JSONObject json = tracks.getJSONObject(i);
            String artistname = json.getJSONObject("artist").getString("name");
            String trackname = json.getString("name");
            String playcount = json.getString("playcount");
            String tracklink = json.getString("url");
            String rank = json.getJSONObject("@attr").getString("rank");

            result[i][0] = rank;
            result[i][1] = artistname;
            result[i][2] = trackname;
            result[i][3] = tracklink;
            result[i][4] = playcount;
            //test if results work
            /*
            System.out.println(result[i][0]);
            System.out.println(result[i][1]);
            System.out.println(result[i][2]);
            System.out.println(result[i][3]);
            System.out.println(result[i][4]);

             */
        }
        resultTracks = result;
    }

    public static String[][] getResultTracks() {
        return resultTracks;
    }



    public static void main(String[] args) {
        //LastFmTopTracksParserTEST tt2 = new LastFmTopTracksParserTEST("c806a80470bbd773b00c2b46b3a1fd75", "abhi_sama", "overall");
        System.out.println(Arrays.deepToString(LastFmTopTracksParserTEST.getResultTracks()));
        //LastFmTopTracksParserTEST tt = new LastFmTopTracksParserTEST("c806a80470bbd773b00c2b46b3a1fd75", "robi874", "overall");
        System.out.println(Arrays.deepToString(LastFmTopTracksParserTEST.getResultTracks()));

    }
}
