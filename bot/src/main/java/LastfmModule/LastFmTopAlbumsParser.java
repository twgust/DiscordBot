package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopAlbumsParser {
    private boolean loaded = false;
    private String[][] topAlbums;

    public LastFmTopAlbumsParser(String username, String apikey, String period/*,  String limit*/) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettopalbums&user=" + username + "&api_key=" + apikey + "&period=" + period + "&limit=" + 1000 + "&format=json"
                    )).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::parse)
                    .join();
        } catch (
                Exception e) {
            e.printStackTrace();
            setLoaded(false);
        }
    }

    public void parse(String responsebody) {


        try {
            JSONObject object = new JSONObject(responsebody);
            JSONObject albumsJSON = object.getJSONObject("topalbums");
            JSONArray albumInfo = albumsJSON.getJSONArray("album");
            String[][] result = new String[albumInfo.length()][6];
            for (int i = 0; i < albumInfo.length(); i++) {
                JSONObject json = albumInfo.getJSONObject(i);
                String playcount = Integer.toString(json.getInt("playcount"));
                JSONObject artist = json.getJSONObject("artist");
                String artistName = artist.getString("name");
                String albumName = json.getString("name");
                JSONArray images = json.getJSONArray("image");
                JSONObject xlImage = images.getJSONObject(3);

                String url = json.getString("url");
                result[i][0] = Integer.toString(i+1);
                result[i][1] = artistName;
                result[i][2] = albumName;
                result[i][3] = playcount;
                if(i == 0){
                    String image = xlImage.getString("#text");
                    result[0][4] = image;
                    if (image.isEmpty()) {
                        image = "https://lastfm.freetls.fastly.net/i/u/300x300/c6f59c1e5e7240a4c0d427abd71f3dbb.png";
                        result[0][4] = image;

                    }
                }

                result[i][5] = url;
            }
            setTopAlbums(result);
            setLoaded(true);
        } catch (Exception e) {
            e.printStackTrace();
            setLoaded(false);
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public String[][] getTopAlbums() {
        return topAlbums;
    }

    public void setTopAlbums(String[][] topAlbums) {
        this.topAlbums = topAlbums;
    }

}


