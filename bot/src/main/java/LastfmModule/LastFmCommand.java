package LastfmModule;

import Commands.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import de.umass.lastfm.Artist;
import de.umass.lastfm.Period;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import org.apache.xerces.parsers.DOMParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.awt.*;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LastFmCommand extends Command {
    private static final String apikey = "c806a80470bbd773b00c2b46b3a1fd75";
    private String discordID;
    private String username;
    private String messageReceived;
    private String[] messageReceivedArr;
    private EmbedBuilder embedMessage;
    private String messageTosend;
    private int maxTrackAmount = 10;
    private String periodStr;
    private MessageEmbed messageEmbed;
    private User user;
    private EventWaiter waiter;
    private Paginator.Builder pbuilder;
    private String noUsernameMessage = "🚫 You have not linked a username to your discordID, use !set (username) to link an account. Type !help for a more detailed instruction 🚫";

    public LastFmCommand(EventWaiter waiter){
        this.waiter = waiter;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        setUsername(null);
        setDiscordID(event.getAuthor().getId());
        setMessageReceived(event.getMessage().getContentRaw());
        setMessageReceivedArr(getMessageReceived().split(" "));
        LastFmSQL sql1 = new LastFmSQL();
        if (getMessageReceivedArr().length == 1)
        {
            if (sql1.checkQuery(getDiscordID()))
            {
                getProfile(sql1.getUsername(discordID), event);
                sql1.closeConnection();
            } else event.getChannel().sendMessage(noUsernameMessage).queue();
        }
        else if (getMessageReceivedArr()[1].equalsIgnoreCase("set"))
        {
            setUsername(getMessageReceivedArr()[2]);
            if (setUsernameInDatabase(getMessageReceivedArr()[2])) {
                sql1.setUsername(getDiscordID(), getUsername());
                sql1.closeConnection();
                event.getChannel().sendMessage(messageTosend).queue(/*message -> {
                    event.getChannel().deleteMessageById(event.getMessageId()).queueAfter(1, TimeUnit.SECONDS);
                    message.delete().queueAfter(1, TimeUnit.SECONDS);
                }*/);
            } else event.getChannel().sendMessage(messageTosend).queue();

        }
        else if (getMessageReceivedArr()[1].equalsIgnoreCase("tt") || getMessageReceivedArr()[1].equalsIgnoreCase("toptracks")) {
            if (sql1.checkQuery(getDiscordID())) {
                if (getMessageReceivedArr().length == 2 || getMessageReceivedArr()[2].equalsIgnoreCase("week") || getMessageReceivedArr()[2].equalsIgnoreCase("w")) {
                    setPeriodStr("7day");
                    if (getMessageReceivedArr().length == 2 || getMessageReceivedArr().length == 3) {
                        topTracks(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                        //ERSSÄTT DETTA MED PBUILDER
                        //event.getChannel().sendMessage(getEmbedMessage().build()).queue();
                    }
                    else if (getMessageReceivedArr().length == 4)
                    {
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                        } catch (NumberFormatException e) {
                            setMaxTrackAmount(10);
                        }

                        topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event);
                        sql1.closeConnection();
                        //ERSÄTT DETTA MED PBUILDER
                        // event.getChannel().sendMessage(getEmbedMessage().build()).queue();
                    }
                }
                else if (getMessageReceivedArr().length == 3 && !getMessageReceivedArr()[2].equalsIgnoreCase("week") && !getMessageReceivedArr()[2].equalsIgnoreCase("w"))
                {
                    setPeriodStr("7day");
                    try {
                        setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[2]));
                    } catch (NumberFormatException e) {
                        setMaxTrackAmount(10);
                        setMessageTosend("Invalid format, try again");
                        sql1.closeConnection();
                        event.getChannel().sendMessage(getMessageTosend()).queue();
                    }
                    topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event);
                    sql1.closeConnection();

                }
                else {
                    topTracks(getDiscordID(), 10, "week", event);
                    sql1.closeConnection();
                }

            }
            else
            {
                event.getChannel().sendMessage(noUsernameMessage).queue();
                sql1.closeConnection();
            }
        } else if (getMessageReceivedArr()[1].equalsIgnoreCase("delete") || getMessageReceivedArr()[1].equalsIgnoreCase("del") || getMessageReceivedArr()[1].equalsIgnoreCase("remove")) {
            if (sql1.checkQuery(getDiscordID())) {

                event.getChannel().sendMessage(messageTosend).queue();
            } else event.getChannel().sendMessage(noUsernameMessage).queue();
        }
        else event.getChannel().sendMessage("Use correct format (HOLDER FOR UPCOMING SHIT POGU)").queue();


    }

    public void topTracks(String discordID, int trackAmount, String periodStr, GuildMessageReceivedEvent event){
        LastFmSQL sql = new LastFmSQL();
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
        decimalFormat.setGroupingSize(3);
        decimalFormat.setGroupingUsed(true);
        String thumbnail = "";
        String username = sql.getUsername(discordID);
        LastFmTopTracksParser tt = new LastFmTopTracksParser(apikey, username, periodStr);
        String[][] tracks = LastFmTopTracksParser.getResultTracks();
        if(trackAmount> tracks.length){
            trackAmount = tracks.length;
        }

        String[] pages = new String[trackAmount];
        int pagetemp = 0;


        for (int i = 0; i < trackAmount; i++ ){
            if(i == 0 ){
                pages[pagetemp] = "";
            }

            String rank = tracks[i][0];
            String artist = tracks[i][1];
            String track = tracks[i][2];
            String tracklink = tracks[i][3];
            int playcount = Integer.parseInt(tracks[i][4]);
            if(artist.contains("*"))

            {
                artist = artist.replace("*", "\\*");
            }
            if(track.contains("*"))
            {
                track = track.replace("*", "\\*");
            }
            if(playcount == 1){
                pages[pagetemp] += "#" +rank + " " + artist  + " - [" +track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
            }
            else pages[pagetemp] += "#" +rank + " " + artist  + " - [" +track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";

            if(pages[pagetemp].length() > 1800){
                pagetemp++;
                pages[pagetemp] = "";
            }
        }
        String[] pagesReal = new String[pagetemp+1];
        for(int j = 0; j <= pagetemp; j++){
            pagesReal[j] = "";
            pagesReal[j] = pages[j];
        }




        String firstArtist = tracks[0][1];
        sql.closeConnection();

        try {
            Document doc = Jsoup.connect("https://www.last.fm/music/" + firstArtist + "/+images").userAgent("Chrome").get();
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
        int page = 1;
        String periodforURL = checkPeriodforURL(periodStr);
        pbuilder = new Paginator.Builder()
                .setColumns(1)
                .setBulkSkipNumber(2000)
                .setItemsPerPage(1)
                .showPageNumbers(true)
                .waitOnSinglePage(false)
                .useNumberedItems(false)
                .setFinalAction(m->{
                    try{
                        m.clearReactions().queue();
                    }catch (PermissionException e){
                        m.delete().queue();
                    }
                })
                .setEventWaiter(waiter)
                .setTimeout(2, TimeUnit.MINUTES);
        pbuilder.clearItems();
        pbuilder.addItems(pagesReal);
        pbuilder.setAuthorText("🎶" +username+"'s top tracks");
        pbuilder.setAuthorURL("https://www.last.fm/user/"+username+"/library/tracks?date_preset="+periodforURL);
        pbuilder.setThumbnail(thumbnail);
        pbuilder.setTitle(getPeriodForBuilder(periodStr));
        Paginator p = pbuilder.setColor(Color.RED)
                .setText("")
                .build();
        p.paginate(event.getChannel(), page);
    }


    public void getProfile(String username, GuildMessageReceivedEvent event) {

        LastFmProfileParser pp = new LastFmProfileParser(username, apikey);
        String [] profile = LastFmProfileParser.getStrings();

        String scrobbles = profile[0];
        String country = profile[1];
        String date = profile[2];
        String thumbnail = profile[3];
        String topArtist = profile[4];
        String topTrack = profile[5];
        String usernameURL = profile[7];


        EmbedBuilder account = new EmbedBuilder();
        account.setColor(0xFF0000);
        account.setAuthor(username + "'s Last.FM Account", usernameURL);
        account.setThumbnail(thumbnail);
        account.addField("Scrobbles", scrobbles, false);
        account.addField("Country", country, false);
        account.addField("Created", date, false);
        account.addField("Top artist", topArtist, false);
        account.addField("Top track", topTrack, true);

        event.getChannel().sendMessage(account.build()).queue();
    }

    public String checkPeriodforURL(String period){
        String periodforURL = "LAST_7_DAYS";
        if (period.equalsIgnoreCase("week") || period.equalsIgnoreCase("7day"))
        {
            periodforURL = "LAST_7_DAYS";
        }
        else if (period.equalsIgnoreCase("1month"))
        {
            periodforURL ="LAST_30_DAYS";
        }
        else if (period.equalsIgnoreCase("month"))
        {
            periodforURL ="LAST_90_DAYS";
        }
        else if (period.equalsIgnoreCase("6month"))
        {
            periodforURL ="LAST_180_DAYS";
        }
        else if (period.equalsIgnoreCase("12month"))
        {
            periodforURL ="LAST_365_DAYS";
        }
        else if (period.equalsIgnoreCase("overall"))
        {
            periodforURL ="ALL";
        }
        return periodforURL;
    }

    public String getPeriodForBuilder(String periodStr){
        if (periodStr.equalsIgnoreCase("week") || periodStr.equalsIgnoreCase("7day")){
            return "Last week";
        }
        else if(periodStr.equalsIgnoreCase("1month")){
            return "Last month";
        }
        else if(periodStr.equalsIgnoreCase("3month")){
            return "Last 3 months";
        }
        else if(periodStr.equalsIgnoreCase("6month")){
            return "Last 6 months";
        }
        else if(periodStr.equalsIgnoreCase("12month")){
            return "Last year";
        }
        else if(periodStr.equalsIgnoreCase("overall")){
            return "Overall";
        }
        else return "Last week";
    }

    public boolean setUsernameInDatabase(String username) {
        String name = username;
        User testUser = null;
        try {
            testUser = User.getInfo(name, apikey);
            //System.out.println(testUser.getName());
            setMessageTosend("Linked last.fm username '" + getUsername() + "' with your discord account ✅");
            return true;
        } catch (Exception e) {
            setMessageTosend("🚫 Username '" + username + "' does not exist 🚫");
            return false;
        }

    }

    public String getDiscordID() {
        return discordID;
    }

    public void setDiscordID(String discordID) {
        this.discordID = discordID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessageReceived() {
        return messageReceived;
    }

    public void setMessageReceived(String messageReceived) {
        this.messageReceived = messageReceived;
    }

    public String[] getMessageReceivedArr() {
        return messageReceivedArr;
    }

    public void setMessageReceivedArr(String[] messageReceivedArr) {
        this.messageReceivedArr = messageReceivedArr;
    }

    public EmbedBuilder getEmbedMessage() {
        return embedMessage;
    }

    public void setEmbedMessage(EmbedBuilder embedMessage) {
        this.embedMessage = embedMessage;
    }

    public String getMessageTosend() {
        return messageTosend;
    }

    public void setMessageTosend(String messageTosend) {
        this.messageTosend = messageTosend;
    }

    public int getMaxTrackAmount() {
        return maxTrackAmount;
    }

    public void setMaxTrackAmount(int maxTrackAmount) {
        this.maxTrackAmount = maxTrackAmount;
    }

    public String getPeriodStr() {
        return periodStr;
    }

    public void setPeriodStr(String periodStr) {
        this.periodStr = periodStr;
    }

    public MessageEmbed getMessageEmbed() {
        return messageEmbed;
    }

    public void setMessageEmbed(MessageEmbed messageEmbed) {
        this.messageEmbed = messageEmbed;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNoUsernameMessage() {
        return noUsernameMessage;
    }

    public void setNoUsernameMessage(String noUsernameMessage) {
        this.noUsernameMessage = noUsernameMessage;
    }
}
