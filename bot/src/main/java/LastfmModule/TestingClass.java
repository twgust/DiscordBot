package LastfmModule;

import Commands.Command;
import com.gargoylesoftware.htmlunit.html.HtmlCanvas;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.sourceforge.htmlunit.cyberneko.HTMLElements;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.commons.io.FileUtils;
import org.apache.html.dom.HTMLBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.xerces.parsers.DOMParser;
import org.eclipse.jetty.io.ssl.ALPNProcessor;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.RenderableImageOp;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TestingClass extends Command {

    private EventWaiter waiter;
    private EmbedBuilder embedBuilder;
    //private final LastfmModule.Paginator.Builder pbuilder;


    public TestingClass(/*EventWaiter waiter*/) {
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
        String[] array = event.getMessage().getContentRaw().split(" ");
        if (array[0].equalsIgnoreCase("%test")) {
            String[] pages = new String[2];
            int pageXD = 0;
            for (int i = 0; i < 20; i++) {
                if (i == 0) {
                    pages[pageXD] = "";
                }


                pages[pageXD] += i + 1 + " Foo Fighters - [Stranger Things Have Happened](https://www.last.fm/music/Foo+Fighters/_/Stranger+Things+Have+Happened) (2 plays) \n";
                if (pages[pageXD].length() > 1800) {
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

    public void testThumbnail() {
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
            Document doc = Jsoup.connect("https://www.last.fm/music/" + firstArtist + "/" + firstAlbum + "/+images").userAgent("Chrome").get();
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
        BufferedImage[] bufferedImages = new BufferedImage[4];
        bufferedImages[0] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg"));
        bufferedImages[2] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg"));
        bufferedImages[1] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/bc85d77d2746baca88059f32c12395ec.jpg"));
        bufferedImages[3] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/bc85d77d2746baca88059f32c12395ec.jpg"));
        BufferedImage c = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);
        try {
            /*
            String test = "test haha lmaoooooooooooooooooooooooooooooo xadxadxdxda";
            Graphics2D g2d = (Graphics2D) c.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            //g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Font font = new Font("Helvetica", Font.PLAIN, 16);
            g2d.setFont(font);
            g2d.setPaint(Color.WHITE);
            g2d.drawString(test, 10, 10);
            g2d.setPaint(Color.BLACK);
            g2d.drawImage(bufferedImages[0], null, 0, 0);

            FontRenderContext frc = new FontRenderContext(null, true, true);
            TextLayout tl = new TextLayout(test, font, frc);
            AffineTransform textAt = new AffineTransform();
            textAt.translate(10, 10);
            Shape outline = tl.getOutline(textAt);
            g2d.setPaint(Color.WHITE);
            g2d.fill(outline);

            g2d.setColor(Color.BLACK);
            BasicStroke wideStroke = new BasicStroke(0);
            g2d.setStroke(wideStroke);


            g2d.draw(outline);

             */
            String htmlbody = "<html>\n" +
                    "  <head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "  </head>\n" +
                    "  <style>\n" +
                    "    div {\n" +
                    "        font-size: 0px;\n" +
                    "        overflow: hidden;\n" +
                    "        text-overflow: ellipsis;\n" +
                    "        white-space: nowrap;\n" +
                    "}\n" +
                    "\n" +
                    "body {\n" +
                    "        display: block;\n" +
                    "        margin: 0px;\n" +
                    "}\n" +
                    "\n" +
                    ".grid {\n" +
                    "\tbackground-color: white;\n" +
                    "}\n" +
                    "\n" +
                    ".container {\n" +
                    "\twidth: 300px;\n" +
                    "\tdisplay: inline-block;\n" +
                    "\tposition: relative;\n" +
                    "}\n" +
                    "\n" +
                    ".text {\n" +
                    "\twidth: 299px;\n" +
                    "\tposition: absolute;\n" +
                    "\ttext-align: left;\n" +
                    "\tline-height: 1;\n" +
                    "\t\n" +
                    "\tfont-family: 'Helvetica', 'Serif';\n" +
                    "\tfont-size: 16px;\n" +
                    "\tfont-weight: medium;\n" +
                    "\tcolor: white;\n" +
                    "\ttext-shadow: 1px 1px black;\n" +
                    "\t\n" +
                    "\ttop: 2px;\n" +
                    " \tleft: 2px;\n" +
                    "\tright:2px;\n" +
                    "}\n" +
                    "  </style>\n" +
                    "  <body>\n" +
                    "    <div class=\"grid\">\n" +
                    "      <div class=\"row\">\n" +
                    "        <div class=\"container\">\n" +
                    "          <img src=\"https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg\" width=300 height=300>\n" +
                    "          <div class=\"text\">\n" +
                    "            artistnameaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa<br>\n" +
                    "            trackname<br>\n" +
                    "            trackamount\n" +
                    "        </div>\n" +
                    "      </div>\n" +
                    "  </body>\n" +
                    "</html>";



            /*
            Graphics2D g2d = (Graphics2D)c.getGraphics();
            FontRenderContext frc = g2d.getFontRenderContext();
            Font font = new Font("Helvetica", Font.PLAIN, 16);
            GlyphVector gv = font.createGlyphVector(frc, "Test");
            Rectangle2D box =  gv.getVisualBounds();
            int xOff = 25+(int)-box.getX();
            int yOFF = 80+(int)-box.getY();
            Shape shape = gv.getOutline(xOff,yOFF);
            g2d.setClip(shape);
            g2d.drawImage(bufferedImages[1], 0,0, null);
            g2d.setClip(null);
            g2d.setStroke(new BasicStroke(0.3f));
            g2d.setColor(Color.BLACK);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.draw(shape);
            g2d.dispose();

            File file = new File("testimages/testingv1.png");
            ImageIO.write(c,"png", file);

             */
            String artist = "test aemkoadwnekldawnekldanwkledawkledlkaw jeawklejaklwedjklaw eaedaweda";
            String track = "akwjdkleaw akwljekl awej klawjeklawj eklawjeklawjekalwje alk klejawkle jawkl";
            String playcount = "12131254125125125124151";
            if(artist.length() > 29){
                char[] array = artist.toCharArray();
                //System.out.println(array);
                array[29] = '.';
                array[28] = '.';
                array[27] = '.';
                StringBuilder sb = sb = new StringBuilder();
                for (int i = 0; i < 30; i++){
                    sb.append(array[i]);
                    //System.out.println(sb);
                }
                artist = sb.toString();
            }
            if(track.length() > 29){
                char[] array = track.toCharArray();
                //System.out.println(array);
                array[29] = '.';
                array[28] = '.';
                array[27] = '.';
                StringBuilder sb = sb = new StringBuilder();
                for (int i = 0; i < 30; i++){
                    sb.append(array[i]);
                    //System.out.println(sb);
                }
                track = sb.toString();
            }


            Graphics2D g2d = (Graphics2D) c.getGraphics();
            g2d.drawImage(bufferedImages[0],0,0,null);
            int x = 5;
            int y = 20;

            //Font font = new Font("Helvetica", Font.BOLD, 16);
            //TextLayout tl = new TextLayout(artist, font, g2d.getFontRenderContext());
            //g2d.setPaint(Color.BLACK);
            //tl.draw(g2d,x,y);
            //g2d.scale(1.2, 1.2);

            //g2d.setPaint(Color.BLACK);
            //tl.draw(g2d,x-4,y-4);

            //g2d.setPaint(Color.WHITE);
            //tl.draw(g2d, x, y );




            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            //g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2d.setBackground(Color.BLACK);
            Font font = new Font("Helvetica", Font.BOLD ,16);
            g2d.setFont(font);
            g2d.setColor(Color.WHITE);
            //g2d.drawString(artist,0,100);
            //g2d.scale(-100,-100);
            FontRenderContext frc = new FontRenderContext(null,false,false);
            TextLayout tl = new TextLayout(artist, font, frc);
            AffineTransform textAt = new AffineTransform();
            textAt.translate(0,20);
            //textAt.scale(0.155,0.155);
            g2d.setColor(Color.GRAY);
            g2d.drawString(artist,+2,20);


            //textAt.scale(0.155,0.155);
            Shape outline = tl.getOutline(textAt);


            g2d.setPaint(Color.WHITE);
            g2d.fill(outline);
            g2d.setColor(Color.BLACK);
            //g2d.setStroke(new CompositeStroke(new BasicStroke(0.25f), new BasicStroke(0.25f)));
            g2d.setStroke(new BasicStroke(0.18f));
            g2d.draw(outline);
            g2d.setColor(Color.WHITE);
            //g2d.drawString(track,0,40);
            FontRenderContext frc2 = new FontRenderContext(null, false, false);
            TextLayout tl2 = new TextLayout(track,font,frc2);
            AffineTransform textAt2 = new AffineTransform();
            g2d.setPaint(Color.BLACK);
            textAt2.translate(0,200);
            Shape outline2 = tl2.getOutline(textAt2);
            g2d.setPaint(Color.WHITE);
            g2d.fill(outline2);
            g2d.setColor(Color.BLACK);
            //g2d.setStroke(new CompositeStroke(new BasicStroke(0.25f), new BasicStroke(0.25f)));
            g2d.draw(outline2);
            //g2d.scale(300,300);







            File file = new File("testimages/testingv1.png");
            ImageIO.write(c,"png", file);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testChartAPI() throws IOException, TranscoderException {
        BufferedImage[] bufferedImages = new BufferedImage[4];
        bufferedImages[0] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg"));
        bufferedImages[2] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg"));
        bufferedImages[1] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/bc85d77d2746baca88059f32c12395ec.jpg"));
        bufferedImages[3] = ImageIO.read(new URL("https://lastfm.freetls.fastly.net/i/u/300x300/bc85d77d2746baca88059f32c12395ec.jpg"));
        BufferedImage c = new BufferedImage(600, 600, BufferedImage.TYPE_INT_ARGB);

        String htmlbody = "<html>\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "  </head>\n" +
                "  <style>\n" +
                "    div {\n" +
                "        font-size: 0px;\n" +
                "        overflow: hidden;\n" +
                "        text-overflow: ellipsis;\n" +
                "        white-space: nowrap;\n" +
                "}\n" +
                "\n" +
                "body {\n" +
                "        display: block;\n" +
                "        margin: 0px;\n" +
                "}\n" +
                "\n" +
                ".grid {\n" +
                "\tbackground-color: white;\n" +
                "}\n" +
                "\n" +
                ".container {\n" +
                "\twidth: 300px;\n" +
                "\tdisplay: inline-block;\n" +
                "\tposition: relative;\n" +
                "}\n" +
                "\n" +
                ".text {\n" +
                "\twidth: 299px;\n" +
                "\tposition: absolute;\n" +
                "\ttext-align: left;\n" +
                "\tline-height: 1;\n" +
                "\t\n" +
                "\tfont-family: 'Helvetica', 'Serif';\n" +
                "\tfont-size: 16px;\n" +
                "\tfont-weight: medium;\n" +
                "\tcolor: white;\n" +
                "\ttext-shadow: 1px 1px black;\n" +
                "\t\n" +
                "\ttop: 2px;\n" +
                " \tleft: 2px;\n" +
                "\tright:2px;\n" +
                "}\n" +
                "  </style>\n" +
                "  <body>\n" +
                "    <div class=\"grid\">\n" +
                "      <div class=\"row\">\n" +
                "        <div class=\"container\">\n" +
                "          <img src=\"https://lastfm.freetls.fastly.net/i/u/300x300/29a0e7984be76ad9bd0b047f7de2242f.jpg\" width=300 height=300>\n" +
                "          <div class=\"text\">\n" +
                "            artistnameaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa<br>\n" +
                "            trackname<br>\n" +
                "            trackamount\n" +
                "        </div>\n" +
                "      </div>\n" +
                "  </body>\n" +
                "</html>";

        JPEGTranscoder transcoder = new JPEGTranscoder();
        transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 0.8f);
        String svgURI = new File("testimages/htmltest.html").toURI().toString();
        TranscoderInput input = new TranscoderInput(svgURI);

        OutputStream os = new FileOutputStream("testimages/xd.png");
        TranscoderOutput output = new TranscoderOutput(os);
        transcoder.transcode(input,output);
        os.flush();
        os.close();

        /*
        File file = new File("testimages/testingv1.png");
        ImageIO.write(c,"png", file);

         */
    }

    public void testGettingImages() {
        String link = "";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(link);

    }

    public void testAPI() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("http://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=c806a80470bbd773b00c2b46b3a1fd75&artist=Aries&track=CAROUSEL&format=json&autocorrect=1&user=robi874")).build();
        CompletableFuture<HttpResponse<String>> test = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        test.thenApply(HttpResponse::body).thenApply(this::test).join();


    }

    public String test(String responsebody) {
        JSONObject jsonObject = new JSONObject(responsebody);
        System.out.println(jsonObject);
        return null;
    }

    public void testMath(){
        int x = 6;
        int y = 10;
        System.out.println(Math.round(Math.sqrt(x*y)));

        int test = 92;
        int result = (int) Math.round(Math.sqrt(92));
        System.out.println(result);

        for (int i = 0; i <10; i+=0){
            System.out.println(i);
        }

    }

    public static void main(String[] args) throws IOException {
        TestingClass test = new TestingClass();
        //test.testChart();
        //test.testGettingImages();
        //test.testAPI();
        test.testMath();
    }
}
