package LastfmModule;

import org.apache.xerces.parsers.DOMParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.Arrays;

public class LastFmYoutube {

    private LastFmRecentTracksParser tracksParser;
    private String[][] track;
    private String[] ytLink = new String[2];
    private boolean loaded = false;
    public LastFmYoutube(String apikey, String username){
        this.tracksParser = new LastFmRecentTracksParser(apikey, username, 1);
        if (tracksParser.isLoaded()){
            this.track = tracksParser.getResultsRecents();
            generateYouTubeLink();
        }
        else loaded = false;

    }

    public void generateYouTubeLink(){
        try {
            String artistname = "No artist";
            String trackname = "No track";
            String lastPlayed = "noplays";
            if(track.length > 0) {
                ytLink[0] = "https://www.youtube.com";
                artistname = track[0][1];
                trackname = track[0][2];
                lastPlayed = "Last played: ";
                if (track[0][4].equalsIgnoreCase("now")){
                    lastPlayed = "Now playing: ";
                }
            }


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
            ytLink[0] += link;
            ytLink[1] = lastPlayed;
            setLoaded(true);
        } catch (Exception e){
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

    public String[] getYtLink() {
        return ytLink;
    }

    public void setYtLink( String[]  ytLink) {
        this.ytLink = ytLink;
    }

    public static void main(String[] args) {
        LastFmYoutube fmYoutube = new LastFmYoutube("c806a80470bbd773b00c2b46b3a1fd75", "robi874");
        System.out.println(Arrays.toString(fmYoutube.getYtLink()));
    }
}
