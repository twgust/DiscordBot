package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopTracksProfileParser {

    private String[][] resultTracks;
    private boolean loaded = false;
    //changed thenAccpet (CLASSNAME::parse) to current to not make it static
    public LastFmTopTracksProfileParser(String apikey, String username) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&period=overall&user=" + username + "&limit=1&api_key=" + apikey + "&format=json")).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::parse)
                    .join();
        }catch (Exception e){
            setLoaded(false);
        }
    }


    public void parse(String responsebody) {

        try {
            JSONObject trackinfo = new JSONObject(responsebody);
            JSONObject toptracks = trackinfo.getJSONObject("toptracks");
            JSONArray tracks = toptracks.getJSONArray("track");
            String[][] result = new String[1][5];
            //System.out.println(tracks);

            String artistname = "";
            String trackname = "";
            String playcount = "";
            String tracklink = "";
            String rank = "";

            try {
                JSONObject json = tracks.getJSONObject(0);
                artistname = json.getJSONObject("artist").getString("name");
                trackname = json.getString("name");
                playcount = json.getString("playcount");
                tracklink = json.getString("url");
                rank = json.getJSONObject("@attr").getString("rank");
            } catch (Exception e) {

            }

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
            setLoaded(true);
        }catch (Exception e){
            e.printStackTrace();
            setLoaded(false);
        }
    }

    public String[][] getResultTracks() {
        return resultTracks;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public static void main(String[] args) {
        LastFmTopTracksProfileParser tt2 = new LastFmTopTracksProfileParser("c806a80470bbd773b00c2b46b3a1fd75", "abhi_sama");
        //System.out.println(Arrays.deepToString(LastFmTopTracksProfileParser.getResultTracks()));
        LastFmTopTracksProfileParser tt = new LastFmTopTracksProfileParser("c806a80470bbd773b00c2b46b3a1fd75", "robi874");
        //System.out.println(Arrays.deepToString(LastFmTopTracksProfileParser.getResultTracks()));

    }
}
