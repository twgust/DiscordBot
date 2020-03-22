package LastfmModule;

import Commands.Command;
import WeatherModule.TimezoneMapper;
import com.jagrosh.jdautilities.menu.Menu;
import de.umass.lastfm.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.apache.xerces.parsers.DOMParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class LastFmCommandOldv2 extends Command {

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
    private String noUsernameMessage = "🚫 You have not linked a username to your discordID, use !set (username) to link an account. Type !help for a more detailed instruction 🚫";

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
                //sql1.listUsers();
                getProfile(sql1.getUsername(discordID));
                sql1.closeConnection();
                event.getChannel().sendMessage(getEmbedMessage().build()).queue();
            } else event.getChannel().sendMessage(noUsernameMessage).queue();
        }
        else if (getMessageReceivedArr()[1].equalsIgnoreCase("set"))
        {
            setUsername(getMessageReceivedArr()[2]);
            if (setUsernameInDatabase(getMessageReceivedArr()[2])) {
                sql1.setUsername(getDiscordID(), getUsername());
                sql1.closeConnection();
                event.getChannel().sendMessage(messageTosend).queue(message -> {
                    event.getChannel().deleteMessageById(event.getMessageId()).queueAfter(1, TimeUnit.SECONDS);
                    message.delete().queueAfter(1, TimeUnit.SECONDS);
                });
            } else event.getChannel().sendMessage(messageTosend).queue();

        }
        else if (getMessageReceivedArr()[1].equalsIgnoreCase("tt") || getMessageReceivedArr()[1].equalsIgnoreCase("toptracks")) {
            if (sql1.checkQuery(getDiscordID())) {
                if (getMessageReceivedArr().length == 2 || getMessageReceivedArr()[2].equalsIgnoreCase("week") || getMessageReceivedArr()[2].equalsIgnoreCase("w")) {
                    setPeriodStr("7day");
                    if (getMessageReceivedArr().length == 2 || getMessageReceivedArr().length == 3) {
                        topTracks(getDiscordID(), 10, getPeriodStr());
                        sql1.closeConnection();
                        event.getChannel().sendMessage(getEmbedMessage().build()).queue();
                    }
                    else if (getMessageReceivedArr().length == 4)
                    {
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                        } catch (NumberFormatException e) {
                            setMaxTrackAmount(10);
                        }

                        topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr());
                        sql1.closeConnection();
                        event.getChannel().sendMessage(getEmbedMessage().build()).queue();
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
                    topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr());
                    sql1.closeConnection();

                    if (getMaxTrackAmount() > 15)
                    {

                        event.getChannel().sendMessage(getEmbedMessage().build()).queue(message -> {
                            message.addReaction("⏮").queue();
                            message.removeReaction("⏮").queueAfter(2, TimeUnit.MINUTES);
                            message.addReaction("⬅").queue();
                            message.removeReaction("⬅").queueAfter(2, TimeUnit.MINUTES);
                            message.addReaction("➡").queue();
                            message.removeReaction("➡").queueAfter(2, TimeUnit.MINUTES);
                            message.addReaction("⏩").queue();
                            message.removeReaction("⏩").queueAfter(2, TimeUnit.MINUTES);
                        });

                    }
                    else {
                        sql1.closeConnection();
                        event.getChannel().sendMessage(getEmbedMessage().build()).queue();
                    }

                }
                else {
                    topTracks(getDiscordID(), 10, "week");
                    sql1.closeConnection();
                    event.getChannel().sendMessage(getEmbedMessage().build()).queue();
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


    public void topTracks(String discordID, int trackAmount, String periodStr) {
        LastFmSQL sql = new LastFmSQL();
        int maxTracks = trackAmount - 1;
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
        decimalFormat.setGroupingSize(3);
        decimalFormat.setGroupingUsed(true);
        EmbedBuilder topTracksBuilder = new EmbedBuilder();
        String thumbnail = "";
        String username = sql.getUsername(discordID);
        String embedInfo = "";
        String dbInfo = "";
        LastFmTopTracksParser tt = new LastFmTopTracksParser(apikey, username, periodStr);
        String[][] tracks = LastFmTopTracksParser.getResultTracks();
        String tracksForEmbed = "";
        for (int i = 0; i < tracks.length; i++ ){
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
            if(i <=maxTracks-1)
            {
                tracksForEmbed  += rank + ". " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
            }

            dbInfo += rank + ". " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "DAB_XD\n";

        }




        String firstArtist = tracks[0][1];
        int pages = (int) (Math.ceil(trackAmount/15.0));

        if (trackAmount > 15){
            maxTracks = 15;
        }

        String [] temp = dbInfo.split("DAB_XD");
        System.out.println(temp.length);
        System.out.println(pages);
        if (pages > (int) (Math.ceil(temp.length/15.0))){
            pages = (int) (Math.ceil(temp.length/15.0));
        }
        System.out.println(pages);
        //System.out.println(dbInfo);
        sql.updateTopTracks(username, dbInfo, periodStr);
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
        String periodforURL = checkPeriodforURL(periodStr);
        topTracksBuilder.setThumbnail(thumbnail);
        topTracksBuilder.setAuthor("🎶" +username+"'s top tracks", "https://www.last.fm/user/robi874/library/tracks?date_preset="+periodforURL);
        topTracksBuilder.setColor(0xFF0000);
        topTracksBuilder.setTitle("Last week top " + trackAmount);
        topTracksBuilder.appendDescription(embedInfo);
        topTracksBuilder.setFooter("Page 1 of " +pages);
        setEmbedMessage(topTracksBuilder);


    }

    public boolean setUsernameInDatabase(String username) {
        String name = username;
        User testUser = null;
        try {
            testUser = User.getInfo(name, getApikey());
            //System.out.println(testUser.getName());
            setMessageTosend("Linked last.fm username '" + getUsername() + "' with your discord account ✅");
            return true;
        } catch (Exception e) {
            setMessageTosend("🚫 Username '" + username + "' does not exist 🚫");
            return false;
        }

    }

    public void getProfile(String username) {

        setUser(User.getInfo(username, apikey));
        try {
            Collection<Artist> topArtistsColl = User.getTopArtists(username, Period.OVERALL, getApikey());
            Collection<Track> topTracksColl = User.getTopTracks(username, Period.OVERALL, getApikey());
            Artist topArtist = topArtistsColl.iterator().next();
            Track topTrack = topTracksColl.iterator().next();

            String artistLink = topArtist.getUrl();
            String trackLink = topTrack.getUrl();
            String artistName = "[" + topArtist.getName() + "]" + "(" + artistLink + ")";
            String trackName = "[" + topTrack.getName() + "]" + "(" + trackLink + ")";
            int artistPlayed = topArtist.getPlaycount();
            int trackPlayed = topTrack.getPlaycount();

            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            formatSymbols.setDecimalSeparator('.');
            formatSymbols.setGroupingSeparator(',');
            DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
            decimalFormat.setGroupingSize(3);
            decimalFormat.setGroupingUsed(true);


            String name = getUser().getName();
            if (name == null) {
                name = "name not found";
            }
            String thumbnail = getUser().getImageURL();
            if (thumbnail == null || thumbnail.isEmpty()) {
                thumbnail = "https://www.last.fm/static/images/lastfm_avatar_twitter.66cd2c48ce03.png";
            }
            int scrobbles = getUser().getPlaycount();
            String country = getUser().getCountry();
            if (country == null) {
                country = "none";
            }

            Date created = getUser().getRegisteredDate();
            Instant createdDate = created.toInstant();
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern("dd MMM yyyy").toFormatter(Locale.ENGLISH);
            ZonedDateTime createdZdt = createdDate.atZone(ZoneId.systemDefault());


            EmbedBuilder account = new EmbedBuilder();
            account.setColor(0xFF0000);
            account.setTitle(name + " Last.FM Account");
            account.setThumbnail(thumbnail);
            account.addField("Scrobbles", decimalFormat.format(scrobbles), true);
            account.addField("Country", country, true);
            account.addField("Created", createdZdt.format(dtf), true);
            account.addField("Top artist", artistName + " (" + decimalFormat.format(artistPlayed) + " plays)", false);
            account.addField("Top track", trackName + " (" + decimalFormat.format(trackPlayed) + " plays)", true);

            setEmbedMessage(account);


        } catch (NullPointerException e) {
            //event.getChannel().sendMessage(noUserNameMessage).queue();
            System.out.println("NullPointerException");
            e.printStackTrace();
        }
    }

    public int charCounter(String str){
        String string = str;
        int count = 0;
        for(int i = 0; i < string.length(); i++){
            if((string.charAt(i) != ' ')){
                count++;
            }
        }
        return count;
    }


    public static String getApikey() {
        return apikey;
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

    public String getPeriodStr() {
        return periodStr;
    }

    public void setPeriodStr(String periodStr) {
        this.periodStr = periodStr;
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

    public String periodForSQL(String period) {
        String periodSQL = "7day";
        if (period.equalsIgnoreCase("week") || period.equalsIgnoreCase("7day")) {
            periodSQL = "7day";
        } else if (period.equalsIgnoreCase("1")) {
            periodSQL = "1month";
        } else if (period.equalsIgnoreCase("3")) {
            periodSQL = "3month";
        } else if (period.equalsIgnoreCase("6")) {
            periodSQL = "6month";
        } else if (period.equalsIgnoreCase("12")) {
            periodSQL = "12month";
        } else if (period.equalsIgnoreCase("overall")) {
            periodSQL = "overall";
        }
        return periodSQL;
    }

    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getReactionEmote().getName().equals("➡") && !event.getMember().getUser().equals(event.getJDA().getSelfUser()) && !event.getMember().getUser().isBot() && event.getChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor().getId().equals("678037870531051531")) {
            String[] checkIfFmCommand;
            try {
                checkIfFmCommand = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getTitle().split(" ");
            }catch (NullPointerException e){
                checkIfFmCommand = new String[1];
                checkIfFmCommand[0] = "";
                System.out.println("TITLE NOT FOUND");
            }
            String[] lastFmAccount;
            String username = "";
            String[] periodFromEmbed;
            String period;
            String[] totalPagesfromEmbed;
            String authorURL = "";
            int totalPages = 0;
            int currentPage = 0;
            String amountOfTracks = "";
            String thumbnail = "";
            if (checkIfFmCommand[0].equalsIgnoreCase("Last"))
            {
                if(checkIfFmCommand[1].equalsIgnoreCase("week")){
                    lastFmAccount = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().split("'");
                    username = lastFmAccount[0].substring(2);
                    periodFromEmbed = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getTitle().split(" ");
                    period = periodFromEmbed[1];
                    totalPagesfromEmbed = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getFooter().getText().split(" ");
                    authorURL = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getAuthor().getUrl();
                    totalPages = Integer.parseInt(totalPagesfromEmbed[3]);
                    currentPage = Integer.parseInt(totalPagesfromEmbed[1]);
                    amountOfTracks = periodFromEmbed[3];
                    thumbnail = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getThumbnail().getUrl();
                    /*
                    System.out.println("username = " + username);
                    System.out.println("period = " + period);
                    System.out.println("currentpage = " + currentPage);
                    System.out.println("totalpages = " + totalPages);
                    System.out.println("total tracks = " + amountOfTracks);
                     */
                    LastFmSQL sql = new LastFmSQL();
                    System.out.println(username);
                    String periodSQL = periodForSQL(period);
                    String [] trackInfo = sql.listUser(periodSQL, username);
                    String[] trackInfoTemp = trackInfo[2].split("DAB_XD");
                    System.out.println(trackInfoTemp.length);
                    String pageInfo = "";
                    String [] pages = new String[totalPages];
                    int page = 1;
                    if(Integer.parseInt(amountOfTracks) > trackInfoTemp.length) {
                        amountOfTracks = Integer.toString(trackInfoTemp.length);
                    }
                    System.out.println(amountOfTracks);
                    for (int i = 0; i <= Integer.parseInt(amountOfTracks)-1; i++){

                        if(i % 15 == 0 && i != 0){
                            page++;
                        }
                        if(i % 15 == 0){
                            pages[page-1] = "";
                        }

                        pages[page - 1] += trackInfoTemp[i];

                    }



                    if (currentPage != totalPages) {
                        /*DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
                        formatSymbols.setDecimalSeparator('.');
                        formatSymbols.setGroupingSeparator(',');
                        DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
                        decimalFormat.setGroupingSize(3);
                        decimalFormat.setGroupingUsed(true);
                        */

                        EmbedBuilder topTracksBuilder = new EmbedBuilder();
                        topTracksBuilder.setAuthor("🎶 " + username + "'s Top tracks", authorURL);
                        topTracksBuilder.setThumbnail(thumbnail);
                        topTracksBuilder.setTitle("Last week top " + amountOfTracks);
                        topTracksBuilder.setColor(0xFF0000);
                        topTracksBuilder.appendDescription(pages[currentPage]);
                        topTracksBuilder.setFooter("Page " + (currentPage + 1) + " of " + totalPages);
                        event.getChannel().retrieveMessageById(event.getMessageId()).complete().editMessage(topTracksBuilder.build()).queue();


                        event.getReaction().removeReaction(event.getUser()).queue();

                    }
                    else event.getReaction().removeReaction(event.getUser()).queue();
                }


            }
            else if(checkIfFmCommand[0].equalsIgnoreCase("Overall")){

            }
            else event.getReaction().removeReaction(event.getUser()).queue();
        }
    }
}

