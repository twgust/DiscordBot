package LastfmModule;

import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LastFmTrackParser {
    private String playcount;
    private boolean loaded;
    //changed thenAccpet (CLASSNAME::parse) to current to not make it static
    public LastFmTrackParser(String artist, String track, String username, String apikey){
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=" + apikey + "&artist=" + artist + "&track=" + track + "&format=json&user=" + username)).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::parse)
                    .join();
        }catch (Exception e){
            e.printStackTrace();
            setLoaded(false);
        }
    }

    public void parse(String responsebody){
        try {
            JSONObject jsonObject = new JSONObject(responsebody);
            JSONObject trackInfo = jsonObject.getJSONObject("track");
            //String playcount = trackInfo.getString("userplaycount");
            String playcount = Integer.toString(trackInfo.getInt("userplaycount"));
            setPlaycount(playcount);
            setLoaded(true);
        }
        catch (Exception e){
            setLoaded(false);
        }
    }

    public String getPlaycount() {
        return playcount;
    }

    public void setPlaycount(String playcount) {
        this.playcount = playcount;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }
}
