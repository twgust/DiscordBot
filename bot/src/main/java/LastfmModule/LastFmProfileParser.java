package LastfmModule;

import org.apache.commons.collections4.set.PredicatedSortedSet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.Locale;

public class LastFmProfileParser {

    private String [] profile;
    private boolean loaded = false;

    //changed thenAccpet (CLASSNAME::parse) to current to not make it static
    public LastFmProfileParser(String username, String apikey){
        try {
            HttpClient client = HttpClient.newHttpClient();
            //"http://ws.audioscrobbler.com/2.0/?method=user.getinfo&user="+username+"&api_key="+apikey+"&format=json";
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.getinfo&user=" + username + "&api_key=" + apikey + "&format=json")).build();
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::parse)
                    .join();
        }catch (Exception e){
            e.printStackTrace();
            setLoaded(false);
        }
    }

    public void parse(String responsebody) {

        try {
            String[] results = new String[8];

            JSONObject artistinfo = new JSONObject(responsebody);
            JSONObject user = artistinfo.getJSONObject("user");
            int playcountTemp = user.getInt("playcount");
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            formatSymbols.setDecimalSeparator('.');
            formatSymbols.setGroupingSeparator(',');
            DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
            decimalFormat.setGroupingSize(3);
            decimalFormat.setGroupingUsed(true);
            String playcount = decimalFormat.format(playcountTemp);
            String country = user.getString("country");
            JSONObject time = user.getJSONObject("registered");
            int timeCreated = time.getInt("unixtime");
            Date created = new Date((long) timeCreated * 1000);
            Instant createdDate = created.toInstant();
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("dd MMM yyyy").toFormatter(Locale.ENGLISH);
            ZonedDateTime createdZdt = createdDate.atZone(ZoneId.systemDefault());
            String date = createdZdt.format(dtf);
            JSONArray images = user.getJSONArray("image");
            String largeimage = "";
            JSONObject image = images.getJSONObject(3);
            largeimage = image.getString("#text");
            if (largeimage.isEmpty()) {
                largeimage = "https://www.last.fm/static/images/lastfm_avatar_twitter.66cd2c48ce03.png";
            }
            String usernameURL = user.getString("url");
            String username = usernameURL.substring(25);
            LastFmTopTracksProfileParser tt = new LastFmTopTracksProfileParser("c806a80470bbd773b00c2b46b3a1fd75", username);
            LastFmTopArtistProfileParser ta = new LastFmTopArtistProfileParser("c806a80470bbd773b00c2b46b3a1fd75", username);
            String topArtist = "Artist not found";
            String topTrack = "Track not found";
            if (ta.isLoaded() && tt.isLoaded()) {

                String[][] tracks = tt.getResultTracks();
                String[][] artists = ta.getResultArtists();
                String playsArtist = "plays";
                String playsTrack = "plays";
                if (artists[0][3].equalsIgnoreCase("1")) {
                    playsArtist = "play";
                }
                if (tracks[0][4].equalsIgnoreCase("1")) {
                    playsTrack = "play";
                }

                try {
                    int artistCount = Integer.parseInt(artists[0][3]);
                    int trackCount = Integer.parseInt(tracks[0][4]);
                    String artistPlaycount = decimalFormat.format(artistCount);
                    String trackPlaycount = decimalFormat.format(trackCount);
                    topArtist = "[" + artists[0][1] + "](" + artists[0][2] + ") (" + artistPlaycount + " " + playsArtist + ")";
                    topTrack = "[" + tracks[0][2] + "](" + tracks[0][3] + ") (" + trackPlaycount + " " + playsTrack + ")";
                } catch (Exception e) {

                }
            }

            results[0] = playcount;
            results[1] = country;
            results[2] = createdZdt.format(dtf);
            results[3] = largeimage;
            results[4] = topArtist;
            results[5] = topTrack;
            results[6] = username;
            results[7] = usernameURL;
        /*System.out.println(playcount);
        System.out.println(country);
        System.out.println(createdZdt.format(dtf));
        System.out.println(largeimage);
        System.out.println(topArtist);
        System.out.println(topTrack);
        System.out.println(username);
        System.out.println(usernameURL);

         */


            profile = results;
            setLoaded(true);
        }catch (Exception e){
            setLoaded(false);
            e.printStackTrace();
        }
    }

    public String[] getProfile() {
        return profile;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public static void main(String[] args) {
        LastFmProfileParser lastFmProfileParser = new LastFmProfileParser("robi874", "c806a80470bbd773b00c2b46b3a1fd75");

    }

}
