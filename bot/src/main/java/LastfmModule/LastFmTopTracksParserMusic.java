package LastfmModule;

import org.apache.xerces.parsers.DOMParser;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

public class LastFmTopTracksParserMusic {

    private String [][] resultTracks;
    private boolean loaded = false;

    //changed thenAccpet (CLASSNAME::parse) to current to not make it static
    public LastFmTopTracksParserMusic (String apikey, String username, String period, int limit) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=user.gettoptracks&period=" + period + "&user=" + username + "&limit="+limit+"&api_key=" + apikey + "&format=json")).build();
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
            String[][] result = new String[tracks.length()][2];
            //System.out.println(tracks);

            for (int i = 0; i < tracks.length(); i++) {
                JSONObject json = tracks.getJSONObject(i);
                String artistname = json.getJSONObject("artist").getString("name");
                String trackname = json.getString("name");
                //result[i][2] = "https://www.youtube.com";
                //String playcount = json.getString("playcount");
                //String tracklink = json.getString("url");
                //String rank = json.getJSONObject("@attr").getString("rank");


                //result[i][0] = rank;
                result[i][0] = artistname;
                result[i][1] = trackname;

                //result[i][3] = tracklink;
                //result[i][4] = playcount;

                //test if results work



                }
                /*
                try{
                    Document doc = Jsoup.connect("https://www.youtube.com/results?search_query=" +artistname + "+"+trackname).userAgent("Chrome").get();
                    Elements images = doc.getElementsByClass("yt-lockup-title");
                    String link = images.get(0).getElementsByTag("a").toString();
                    InputSource is = new InputSource(new StringReader(link));
                    DOMParser dp = new DOMParser();
                    dp.parse(is);
                    org.w3c.dom.Document document = dp.getDocument();
                    NodeList nl = document.getElementsByTagName("a");
                    Node n = nl.item(0);
                    NamedNodeMap nnm = n.getAttributes();
                    link = nnm.getNamedItem("href").getFirstChild().getTextContent();
                    System.out.println(link);
                    result[i][2] += link;
                } catch (Exception e){
                    e.printStackTrace();
                }

                 */

            /*
            System.out.println(result[i][0]);
            System.out.println(result[i][1]);
            System.out.println(result[i][2]);
            System.out.println(result[i][3]);
            System.out.println(result[i][4]);

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
        LastFmTopTracksParserMusic topTracksParserMusic = new LastFmTopTracksParserMusic("c806a80470bbd773b00c2b46b3a1fd75", "robi874", "week", 5);
        System.out.println(Arrays.deepToString(topTracksParserMusic.getResultTracks()));
    }
}
