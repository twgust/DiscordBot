package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopArtistProfileParser {

    private String[][] resultArtists;
    private boolean loaded = false;
    //changed thenAccpet (CLASSNAME::parse) to current to not make it static
    public LastFmTopArtistProfileParser(String apikey, String username) {
        try {

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettopartists&period=overall&user=" + username + "&limit=1&api_key=" + apikey + "&format=json")).build();
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
            } catch (Exception e) {

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
            setLoaded(true);
        }catch (Exception e){
            e.printStackTrace();
            setLoaded(false);
        }
    }

    public String[][] getResultArtists() {
        return resultArtists;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }



}
