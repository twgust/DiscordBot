package LastfmModule;

import Commands.Command;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.sourceforge.htmlunit.cyberneko.HTMLElements;
import org.apache.html.dom.HTMLBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xerces.parsers.DOMParser;
import org.eclipse.jetty.io.ssl.ALPNProcessor;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TestingClass extends Command {

    private EventWaiter waiter;
    private EmbedBuilder embedBuilder;
    //private final LastfmModule.Paginator.Builder pbuilder;


    public TestingClass(/*EventWaiter waiter*/){
        /*this.waiter = waiter;
        pbuilder = new Paginator.Builder()

                .setColumns(1)
                .setBulkSkipNumber(2000)
                .setItemsPerPage(1)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(false)
                .setFinalAction(m ->{
                    try {
                        m.clearReactions().queue();
                    }catch (PermissionException ex){
                        m.delete().queue();
                    }

                })
                .setEventWaiter(waiter)
                .setTimeout(2, TimeUnit.MINUTES);

         */


    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        int page = 1;
        String [] array = event.getMessage().getContentRaw().split(" ");
        if(array[0].equalsIgnoreCase("%test")){
            String [] pages = new String[2];
            int pageXD = 0;
            for(int i = 0; i < 20; i++){
                if(i == 0){
                    pages[pageXD] = "";
                }



                pages[pageXD] += i+1 + " Foo Fighters - [Stranger Things Have Happened](https://www.last.fm/music/Foo+Fighters/_/Stranger+Things+Have+Happened) (2 plays) \n";
                if(pages[pageXD].length() > 1800){
                    pageXD++;
                    pages[pageXD] = "";
                }


            }

            /*
            pbuilder.clearItems();
            pbuilder.addItems(pages);
            pbuilder.setAuthorText("test");
            pbuilder.setAuthorURL("https://www.last.fm/user/robi874");
            pbuilder.setThumbnail("https://ih1.redbubble.net/image.411594717.6096/ap,550x550,12x16,1,transparent,t.u1.png");

            LastfmModule.Paginator p = pbuilder.setColor(Color.RED)
                    .setText("")
                    .build();
            p.paginate(event.getChannel(), page);

             */



        }
    }

    public void testThumbnail(){
        String thumbnail;
        String firstArtist = "DAVICHI";
        String firstAlbum = "Crash Landing on You (Original Television Soundtrack), Pt. 3";
        try {//https://www.last.fm/music/DAVICHI/Crash+Landing+on+You+(Original+Television+Soundtrack),+Pt.+3

            /*
            final WebClient webClient = new WebClient();
            final HtmlPage page = webClient.getPage("https://www.last.fm/music/"+firstArtist+"/"+firstAlbum+ "/+images");
            page.getElementByName("image-list-item-wrapper");
            System.out.println(page.getElementByName("image-list-item-wrapper"));

             */


            System.out.println("try to connect");
            Document doc = Jsoup.connect("https://www.last.fm/music/"+firstArtist+"/"+firstAlbum+ "/+images").userAgent("Chrome").get();
            System.out.println("connected");
            Elements images = doc.getElementsByClass("image-list-item-wrapper");
            String imagelink = images.get(0).getElementsByTag("img").toString();
            imagelink = imagelink.replace("/avatar170s/", "/450x450/");
            imagelink += "</img>";

            InputSource is = new InputSource(new StringReader(imagelink));
            DOMParser dp = new DOMParser();
            dp.parse(is);
            org.w3c.dom.Document document = dp.getDocument();
            NodeList nl = document.getElementsByTagName("img");
            Node n = nl.item(0);
            NamedNodeMap nnm = n.getAttributes();
            thumbnail = nnm.getNamedItem("src").getFirstChild().getTextContent();

        } catch (NullPointerException | IOException | SAXException e) {
            thumbnail = "https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png";
            e.printStackTrace();
        }
        System.out.println(thumbnail);
    }

    public void testChart() throws IOException {
        BufferedImage [] bufferedImages = new BufferedImage[4];
        bufferedImages[0] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg"));
        bufferedImages[2] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg"));
        bufferedImages[1] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/bc85d77d2746baca88059f32c12395ec.jpg"));
        bufferedImages[3] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/bc85d77d2746baca88059f32c12395ec.jpg"));
        BufferedImage c = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        try {
            String htmlString = "`<html>\n`, +<head>\n, +`    <meta charset=UTF-8>\n`, +`</head>\n\n`, +`<style>\n`, +`${css}\n`,`</style>\n\n`,`<body>\n`,`${htmlString}\n`,`</body>\n\n`,`</html>\n`";
            for (int i = 0; i < 4; i++) {



            }


            File outputFile = new File("saved.png");
            ImageIO.write(c, "png", outputFile);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void testGettingImages(){
        String link ="";
        try {
            Document doc = Jsoup.connect("https://www.last.fm/user/robi874/library/artists?date_preset=LAST_7_DAYS").userAgent("Chrome").get();
            Elements images = doc.getElementsByClass("chartlist--with-image");
            System.out.println();
            //for(int i = 0; i < 20; i ++0)
            String imagelink = images.get(0).getElementsByClass("avatar").get(0).getElementsByTag("img").toString();//getElementsByTag("img").toString();
            imagelink = imagelink.replace("/avatar70s/", "/300x300/");
            imagelink += "</img>";

            InputSource is = new InputSource(new StringReader(imagelink));
            DOMParser dp = new DOMParser();
            dp.parse(is);
            org.w3c.dom.Document document = dp.getDocument();
            NodeList nl = document.getElementsByTagName("img");
            Node n = nl.item(0);
            NamedNodeMap nnm = n.getAttributes();
            link = nnm.getNamedItem("src").getFirstChild().getTextContent();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(link);

    }

    public void testAPI(){
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=c806a80470bbd773b00c2b46b3a1fd75&artist=Aries&track=CAROUSEL&format=json&autocorrect=1&user=robi874")).build();
        CompletableFuture<HttpResponse<String>> test = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        test.thenApply(HttpResponse::body).thenApply(this::test).join();


    }
    public String test(String responsebody){
        JSONObject jsonObject = new JSONObject(responsebody);
        System.out.println(jsonObject);
        return null;
    }

    public static void main(String[] args) throws IOException {
        TestingClass test = new TestingClass();
        test.testChart();
        //test.testGettingImages();
        //test.testAPI();
    }
}
