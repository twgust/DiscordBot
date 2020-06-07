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

public class LastFmNowPlayingParser {

    private String[][] nowplayingInfo;
    private String username = "";
    private boolean loaded = false;

    //changed thenAccpet (CLASSNAME::parse) to current to not make it static
    public LastFmNowPlayingParser(String apikey, String username) {
        try {
            setUsername(username);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=" + username + "&period=overall&api_key=" + apikey + "&format=json&limit=2")).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::parse)
                    .join();
        } catch (Exception e) {
            e.printStackTrace();
            setLoaded(false);
        }

    }

    public void parse(String responsebody) {
        try {
            String[][] result = new String[2][8];
            JSONObject recenttracksinfo = new JSONObject(responsebody);
            JSONObject recentracks = recenttracksinfo.getJSONObject("recenttracks");
            JSONArray tracks = recentracks.getJSONArray("track");

            JSONObject attr = recentracks.getJSONObject("@attr");
            String playcount = attr.getString("total");

            int length = 2;
            if (tracks.length() == 1) {
                length = 1;
            }
            if(tracks.length() !=0) {
                for (int i = 0; i < length; i++) {
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
                            e.printStackTrace();
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
                            } else timeAgo = "N/A";
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
                    String artistTemp = result[0][1];
                    String trackTemp = result[0][2];
                    if (artistTemp.contains(" ") || artistTemp.contains("%")) {
                        artistTemp = artistTemp.replace(" ", "+");
                        artistTemp = artistTemp.replace("%", "%25");
                    }
                    if (trackTemp.contains(" ") || trackTemp.contains("%")) {
                        trackTemp = trackTemp.replace(" ", "+");
                        trackTemp = trackTemp.replace("%", "%25");
                    }

                    LastFmTrackParser trackParser = new LastFmTrackParser(artistTemp, trackTemp, getUsername(), "7c8cf63953bf97c9cbd89ca81e6e34c0");
                    if (trackParser.isLoaded()) {
                        result[0][7] = trackParser.getPlaycount();
                    } else result[0][7] = "Failed to load";
                }
            }else {
                result[0][0] = ("not loaded");
            }

            /*
            String artistTemp = result[0][1];
            String trackTemp = result[0][2];
            if (artistTemp.contains(" ")) {
                artistTemp = artistTemp.replace(" ", "+");
            }
            if (trackTemp.contains(" ")) {
                trackTemp = trackTemp.replace(" ", "+");
            }

            LastFmTrackParser trackParser = new LastFmTrackParser(artistTemp, trackTemp, getUsername(), "7c8cf63953bf97c9cbd89ca81e6e34c0");
            if(trackParser.isLoaded()){
                result[0][7] = trackParser.getPlaycount();
            }
            else result[0][7] = "Failed to load";

             */


        /*
        System.out.println(result[0][0]);
        System.out.println(result[0][1]);
        System.out.println(result[0][2]);
        System.out.println(result[0][3]);
        System.out.println(result[0][4]);
        System.out.println(result[0][5]);
        System.out.println(result[0][6]);
        System.out.println(result[0][7]);
        System.out.println(result[1][1]);
        System.out.println(result[1][2]);
        System.out.println(result[1][3]);
        System.out.println(result[1][4]);
        System.out.println(result[1][5]);
        System.out.println(result[1][6]);

         */
            setNowplayingInfo(result);
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String[][] getNowplayingInfo() {
        return nowplayingInfo;
    }

    public void setNowplayingInfo(String[][] nowplayingInfo) {
        this.nowplayingInfo = nowplayingInfo;
    }


}
