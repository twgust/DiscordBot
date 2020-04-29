package LastfmModule;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class LastFmRecentTracksParser {

    private String[][] resultsRecents;
    private int trackamount;
    private boolean loaded = false;

    public LastFmRecentTracksParser(String apikey, String username, int trackamount){
        try {
            this.trackamount = trackamount;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=" + username + "&period=overall&limit="+trackamount+"&api_key=" + apikey + "&format=json")).build();
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

            JSONObject recenttracksinfo = new JSONObject(responsebody);
            JSONObject recentracks = recenttracksinfo.getJSONObject("recenttracks");
            JSONArray tracks = recentracks.getJSONArray("track");
            if (tracks.length() < trackamount) {
                trackamount = tracks.length();
            }

            String[][] result = new String[trackamount][7];
            JSONObject attr = recentracks.getJSONObject("@attr");
            String playcount = attr.getString("total");

            for (int i = 0; i < trackamount; i++) {
                JSONObject json = tracks.getJSONObject(i);
                JSONObject artistJS;
                String artistname = "";
                String tracklink = "";
                String trackname = "";
                String timeAgo = "";
                JSONObject date;
                JSONArray albumJS;
                String album = "";
                long time = 0;
                Date dateTemp;


                if (i == 0) {
                    try {
                        artistJS = json.getJSONObject("artist");
                        artistname = artistJS.getString("#text");
                        tracklink = json.getString("url");
                        trackname = json.getString("name");
                        date = json.getJSONObject("date");
                        albumJS = json.getJSONArray("image");
                        for (int x = 0; x < 2; x++) {
                            JSONObject jsonObject = albumJS.getJSONObject(3);
                            album = jsonObject.getString("#text");
                        }

                        time = date.getLong("uts") * 1000;
                        dateTemp = new Date();
                        dateTemp.setTime(time);
                        PrettyTime p = new PrettyTime(new Locale("en"));
                        timeAgo = p.format(dateTemp);
                        result[i][0] = Integer.toString(i + 1);
                        result[i][1] = artistname;
                        result[i][2] = trackname;
                        result[i][3] = tracklink;
                        result[i][4] = timeAgo;
                        result[0][5] = playcount;
                        result[0][6] = album;

                    } catch (Exception e) {
                        artistJS = json.getJSONObject("artist");
                        artistname = artistJS.getString("#text");
                        tracklink = json.getString("url");
                        trackname = json.getString("name");
                        date = json.getJSONObject("@attr");
                        String dateStr = date.getString("nowplaying");
                        albumJS = json.getJSONArray("image");
                        for (int x = 0; x < 2; x++) {
                            JSONObject jsonObject = albumJS.getJSONObject(3);
                            album = jsonObject.getString("#text");
                        }
                        if (dateStr.equalsIgnoreCase("true")) {
                            timeAgo = "now";
                        } else timeAgo = "404";
                        result[i][0] = Integer.toString(i + 1);
                        result[i][1] = artistname;
                        result[i][2] = trackname;
                        result[i][3] = tracklink;
                        result[i][4] = timeAgo;
                        result[0][5] = playcount;
                        result[0][6] = album;
                    }
                } else {
                    artistJS = json.getJSONObject("artist");
                    artistname = artistJS.getString("#text");
                    tracklink = json.getString("url");
                    trackname = json.getString("name");
                    date = json.getJSONObject("date");
                /*
                albumJS = json.getJSONObject("album");
                album = albumJS.getString("#text");

                 */
                    time = date.getLong("uts") * 1000;
                    dateTemp = new Date();
                    dateTemp.setTime(time);
                    PrettyTime p = new PrettyTime(new Locale("en"));
                    timeAgo = p.format(dateTemp);
                    dateTemp.setTime(time);
                    result[i][0] = Integer.toString(i + 1);
                    result[i][1] = artistname;
                    result[i][2] = trackname;
                    result[i][3] = tracklink;
                    result[i][4] = timeAgo;
                }


                //test if results work
            /*
            System.out.println(result[i][0]);
            System.out.println(result[i][1]);
            System.out.println(result[i][2]);
            System.out.println(result[i][3]);
            System.out.println(result[i][4]);
            System.out.println(result[i][5]);
            System.out.println(result[i][6]);

             */


            }

            resultsRecents = result;
            setLoaded(true);
        }catch (Exception e){
            e.printStackTrace();
            setLoaded(false);
        }
    }

    public String[][] getResultsRecents() {
        return resultsRecents;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public static void main(String[] args) {
        LastFmRecentTracksParser rt = new LastFmRecentTracksParser("c806a80470bbd773b00c2b46b3a1fd75", "robi874", 10);
        System.out.println(Arrays.deepToString(rt.getResultsRecents()));
    }
}
