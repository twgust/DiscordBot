package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopTracksParser {

    private String [][] resultTracks;
    private boolean loaded = false;

    //changed thenAccpet (CLASSNAME::parse) to current to not make it static
    public LastFmTopTracksParser(String apikey, String username, String period) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&period=" + period + "&user=" + username + "&limit=1000&api_key=" + apikey + "&format=json")).build();
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
            String[][] result = new String[tracks.length()][5];
            //System.out.println(tracks);

            for (int i = 0; i < tracks.length(); i++) {
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


}
