package LastfmModule;

import Commands.Command;
import Main.EventListener;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import de.umass.lastfm.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.internal.requests.Route;
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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
    private String noUsernameMessage = "```❌ You've not linked your lastfm username. Type "+EventListener.prefix+"fm set <username>. Type "+EventListener.prefix+"help for more help noob. ❌```";
    private String wrongFormatMessage = "```❌ Invalid format, try again. Type "+ EventListener.prefix +"help for more help noob ❌```";

    public LastFmCommand(EventWaiter waiter) {
        this.waiter = waiter;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        setUsername(null);
        setDiscordID(event.getAuthor().getId());
        setMessageReceived(event.getMessage().getContentRaw());
        setMessageReceivedArr(getMessageReceived().split(" "));
        LastFmSQL sql1 = new LastFmSQL();
        if (getMessageReceivedArr().length == 1) {
            if (sql1.checkQuery(getDiscordID())) {
                getProfile(sql1.getUsername(getDiscordID()), event);
                sql1.closeConnection();
            } else event.getChannel().sendMessage(noUsernameMessage).queue();
        }
        else if (getMessageReceivedArr().length == 2) {
            if(getMessageReceivedArr()[1].equalsIgnoreCase("tt") || getMessageReceivedArr()[1].equalsIgnoreCase("toptracks")){
                if(sql1.checkQuery(getDiscordID())){
                    setPeriodStr("7day");
                    topTracks(getDiscordID(),10, getPeriodStr(), event);
                    sql1.closeConnection();
                }
                else event.getChannel().sendMessage(noUsernameMessage).queue(); sql1.closeConnection();
            }
            else if (getMessageReceivedArr()[1].equalsIgnoreCase("ta") || getMessageReceivedArr()[1].equalsIgnoreCase("topartists")){
                if (sql1.checkQuery(getDiscordID())){
                    setPeriodStr("7day");
                    topArtists(getDiscordID(), 10, getPeriodStr(), event);
                    sql1.closeConnection();
                }
                else event.getChannel().sendMessage(noUsernameMessage).queue(); sql1.closeConnection();
            }
            else if (getMessageReceivedArr()[1].contains("<@!")){
                net.dv8tion.jda.api.entities.User user = event.getMessage().getMentionedUsers().get(0);
                if (sql1.checkQuery(user.getId())){
                    getProfile(sql1.getUsername(user.getId()), event);
                    sql1.closeConnection();
                }
                else {
                    setMessageTosend("```❌ No username linked to discord account. ❌```");
                    event.getChannel().sendMessage(getMessageTosend()).queue();
                    sql1.closeConnection();
                }
            }
            else if (getMessageReceivedArr()[1].equalsIgnoreCase("delete") || getMessageReceivedArr()[1].equalsIgnoreCase("del") || getMessageReceivedArr()[1].equalsIgnoreCase("remove")) {
                if (sql1.checkQuery(getDiscordID())) {
                    sql1.closeConnection();
                    deleteUsernameInSQL(getDiscordID(), event);
                }
                else {
                    event.getChannel().sendMessage(noUsernameMessage).queue();
                    sql1.closeConnection();
                }
            }
            else if (getMessageReceivedArr()[1].equalsIgnoreCase("recent") || getMessageReceivedArr()[1].equalsIgnoreCase("rt")){
                if(sql1.checkQuery(getDiscordID())){
                    sql1.closeConnection();
                    getRecentTracks(getDiscordID(),10, event);
                }
                else {
                    event.getChannel().sendMessage(noUsernameMessage).queue();
                    sql1.closeConnection();
                }
            }
            else  {
                if (checkIfUserExist(getMessageReceivedArr()[1])){
                    getProfile(getMessageReceivedArr()[1], event);
                    sql1.closeConnection();
                }
                else {
                    event.getChannel().sendMessage("```❌ Username '" + getMessageReceivedArr()[1] +"' does not exist ❌```").queue();
                    sql1.closeConnection();
                }
            }

        }
        else if (getMessageReceivedArr().length == 3) {
            if (getMessageReceivedArr()[1].equalsIgnoreCase("set")) {
                setUsername(getMessageReceivedArr()[2]);
                if (setUsernameInDatabase(getMessageReceivedArr()[2])) {
                    sql1.setUsername(getDiscordID(), getUsername());
                    sql1.closeConnection();
                }
                event.getChannel().sendMessage(messageTosend).queue();
            }
            else if(getMessageReceivedArr()[1].equalsIgnoreCase("tt") || getMessageReceivedArr()[1].equalsIgnoreCase("toptracks")) {
                if(sql1.checkQuery(getDiscordID())){
                    if(getMessageReceivedArr()[2].equalsIgnoreCase("w") || getMessageReceivedArr()[2].equalsIgnoreCase("week")){
                        setPeriodStr("7day");
                        topTracks(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("m") || getMessageReceivedArr()[2].equalsIgnoreCase("month")){
                        setPeriodStr("1month");
                        topTracks(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("3m") || getMessageReceivedArr()[2].equalsIgnoreCase("3months")){
                        setPeriodStr("3month");
                        topTracks(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("6m") || getMessageReceivedArr()[2].equalsIgnoreCase("6months")){
                        setPeriodStr("6month");
                        topTracks(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("y") || getMessageReceivedArr()[2].equalsIgnoreCase("year") || getMessageReceivedArr()[2].equalsIgnoreCase("12m") || getMessageReceivedArr()[2].equalsIgnoreCase("12months")){
                        setPeriodStr("12month");
                        topTracks(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("all") || getMessageReceivedArr()[2].equalsIgnoreCase("overall") || getMessageReceivedArr()[2].equalsIgnoreCase("at") || getMessageReceivedArr()[2].equalsIgnoreCase("alltime")){
                        setPeriodStr("overall");
                        topTracks(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else {
                        setPeriodStr("7day");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[2]));
                            topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            sql1.closeConnection();
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                        }
                    }
                }
                else event.getChannel().sendMessage(noUsernameMessage).queue();
            }
            else if (getMessageReceivedArr()[1].equalsIgnoreCase("ta") || getMessageReceivedArr()[1].equalsIgnoreCase("topartists")){
                if(sql1.checkQuery(getDiscordID())){
                    if(getMessageReceivedArr()[2].equalsIgnoreCase("w") || getMessageReceivedArr()[2].equalsIgnoreCase("week")){
                        setPeriodStr("7day");
                        topArtists(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("m") || getMessageReceivedArr()[2].equalsIgnoreCase("month")){
                        setPeriodStr("1month");
                        topArtists(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("3m") || getMessageReceivedArr()[2].equalsIgnoreCase("3months")){
                        setPeriodStr("3month");
                        topArtists(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("6m") || getMessageReceivedArr()[2].equalsIgnoreCase("6months")){
                        setPeriodStr("6month");
                        topArtists(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("y") || getMessageReceivedArr()[2].equalsIgnoreCase("year") || getMessageReceivedArr()[2].equalsIgnoreCase("12m") || getMessageReceivedArr()[2].equalsIgnoreCase("12months")){
                        setPeriodStr("12month");
                        topArtists(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("all") || getMessageReceivedArr()[2].equalsIgnoreCase("overall") || getMessageReceivedArr()[2].equalsIgnoreCase("at") || getMessageReceivedArr()[2].equalsIgnoreCase("alltime")){
                        setPeriodStr("overall");
                        topArtists(getDiscordID(), 10, getPeriodStr(), event);
                        sql1.closeConnection();
                    }
                    else {
                        setPeriodStr("7day");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[2]));
                            topArtists(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            sql1.closeConnection();
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                        }
                    }
                }
                else event.getChannel().sendMessage(noUsernameMessage).queue();
            }
            else if(getMessageReceivedArr()[1].equalsIgnoreCase("recent") || getMessageReceivedArr()[1].equalsIgnoreCase("rt")){
                if ((sql1.checkQuery(getDiscordID()))){
                    sql1.closeConnection();
                    try {
                        setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[2]));
                        getRecentTracks(getDiscordID(), getMaxTrackAmount(), event);
                    }catch (NumberFormatException e){
                        event.getChannel().sendMessage(wrongFormatMessage).queue();
                    }
                }
                else {
                    sql1.closeConnection();
                    event.getChannel().sendMessage(noUsernameMessage).queue();
                }
            }

            else {
                sql1.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }

        }else if (getMessageReceivedArr().length == 4){
            if(getMessageReceivedArr()[1].equalsIgnoreCase("tt") || getMessageReceivedArr()[1].equalsIgnoreCase("toptracks")){
                if(sql1.checkQuery(getDiscordID())){
                    if(getMessageReceivedArr()[2].equalsIgnoreCase("w") || getMessageReceivedArr()[2].equalsIgnoreCase("week")){
                        setPeriodStr("7day");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[4]));
                            topTracks(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("m") || getMessageReceivedArr()[2].equalsIgnoreCase("month")){
                        setPeriodStr("1month");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topTracks(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("3m") || getMessageReceivedArr()[2].equalsIgnoreCase("3months")){
                        setPeriodStr("3month");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topTracks(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("6m") || getMessageReceivedArr()[2].equalsIgnoreCase("6months")){
                        setPeriodStr("6month");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topTracks(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("y") || getMessageReceivedArr()[2].equalsIgnoreCase("year") || getMessageReceivedArr()[2].equalsIgnoreCase("12m") || getMessageReceivedArr()[2].equalsIgnoreCase("12months")){
                        setPeriodStr("12month");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topTracks(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("all") || getMessageReceivedArr()[2].equalsIgnoreCase("overall") || getMessageReceivedArr()[2].equalsIgnoreCase("at") || getMessageReceivedArr()[2].equalsIgnoreCase("alltime")){
                        setPeriodStr("overall");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topTracks(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else {
                        event.getChannel().sendMessage(wrongFormatMessage).queue();
                        sql1.closeConnection();
                    }
                }else {
                    event.getChannel().sendMessage(noUsernameMessage).queue();
                    sql1.closeConnection();
                }
            }
            else if (getMessageReceivedArr()[1].equalsIgnoreCase("ta") || getMessageReceivedArr()[1].equalsIgnoreCase("topartists")){
                if(sql1.checkQuery(getDiscordID())){
                    if(getMessageReceivedArr()[2].equalsIgnoreCase("w") || getMessageReceivedArr()[2].equalsIgnoreCase("week")){
                        setPeriodStr("7day");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topArtists(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("m") || getMessageReceivedArr()[2].equalsIgnoreCase("month")){
                        setPeriodStr("1month");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topArtists(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("3m") || getMessageReceivedArr()[2].equalsIgnoreCase("3months")){
                        setPeriodStr("3month");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topArtists(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("6m") || getMessageReceivedArr()[2].equalsIgnoreCase("6months")){
                        setPeriodStr("6month");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topArtists(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("y") || getMessageReceivedArr()[2].equalsIgnoreCase("year") || getMessageReceivedArr()[2].equalsIgnoreCase("12m") || getMessageReceivedArr()[2].equalsIgnoreCase("12months")){
                        setPeriodStr("12month");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topArtists(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else if (getMessageReceivedArr()[2].equalsIgnoreCase("all") || getMessageReceivedArr()[2].equalsIgnoreCase("overall") || getMessageReceivedArr()[2].equalsIgnoreCase("at") || getMessageReceivedArr()[2].equalsIgnoreCase("alltime")){
                        setPeriodStr("overall");
                        try {
                            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                            topArtists(getDiscordID(), getMaxTrackAmount(),getPeriodStr(), event);
                            sql1.closeConnection();
                        } catch (NumberFormatException e){
                            setMaxTrackAmount(10);
                            event.getChannel().sendMessage(wrongFormatMessage).queue();
                            sql1.closeConnection();
                        }
                    }
                    else {
                        event.getChannel().sendMessage(wrongFormatMessage).queue();
                        sql1.closeConnection();
                    }
                } else {
                    event.getChannel().sendMessage(noUsernameMessage).queue();
                    sql1.closeConnection();
                }
            }

        }
        else event.getChannel().sendMessage("Use correct format (HOLDER FOR UPCOMING SHIT POGU)").queue();


    }

    public void topTracks(String discordID, int trackAmount, String periodStr, GuildMessageReceivedEvent event) {
        AtomicInteger trackAmountTemp = new AtomicInteger(trackAmount);

        event.getChannel().sendMessage("```Loading data...```").queue(message -> {
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
            try {

                if (trackAmountTemp.get() > tracks.length) {
                    trackAmountTemp.set(tracks.length);
                }

                String[] pages = new String[trackAmountTemp.get()];
                int pagetemp = 0;

                int totalPlaycount = 0;
                for (String[] strings : tracks) {
                    totalPlaycount += Integer.parseInt(strings[4]);
                }
                for (int i = 0; i < trackAmountTemp.get(); i++) {
                    if (i == 0) {
                        pages[pagetemp] = "";
                    }

                    String rank = tracks[i][0];
                    String artist = tracks[i][1];
                    String track = tracks[i][2];
                    String tracklink = tracks[i][3];
                    int playcount = Integer.parseInt(tracks[i][4]);
                    if (artist.contains("*")) {
                        artist = artist.replace("*", "\\*");
                    }
                    if (track.contains("*")) {
                        track = track.replace("*", "\\*");
                    }
                    if(i == 0){
                        if(playcount == 1){
                            pages[pagetemp] += "🥇 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";

                        }
                        else pages[pagetemp] += "🥇 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                    }
                    else if(i==1){
                        if(playcount == 1){
                            pages[pagetemp] += "🥈 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                        }
                        else pages[pagetemp] += "🥈 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                    }
                    else if (i == 2){
                        if(playcount == 1){
                            pages[pagetemp] += "🥉 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                        }
                        else pages[pagetemp] += "🥉 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                    }
                    else {

                        if (playcount == 1) {
                            pages[pagetemp] += "#" + rank + " " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                        }
                        else pages[pagetemp] += "#" + rank + " " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";

                    }

                    if (pages[pagetemp].length() > 1800 && tracks.length-1 != i) {
                        pagetemp++;
                        pages[pagetemp] = "";
                    }
                }


                String[] pagesReal = new String[pagetemp + 1];
                for (int j = 0; j <= pagetemp; j++) {
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
                    System.out.println("could not load image");
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
                        .setFinalAction(m -> {
                            try {
                                m.clearReactions().queue();
                            } catch (PermissionException e) {
                                m.delete().queue();
                            }
                        })
                        .setEventWaiter(waiter)
                        .setTimeout(2, TimeUnit.MINUTES);
                pbuilder.clearItems();
                pbuilder.addItems(pagesReal);
                pbuilder.setAuthorText("🎶" + username + "'s top tracks");
                pbuilder.setAuthorURL("https://www.last.fm/user/" + username + "/library/tracks?date_preset=" + periodforURL);
                pbuilder.setAuthorIconURL(event.getAuthor().getAvatarUrl());
                pbuilder.setThumbnail(thumbnail);
                pbuilder.setTitle(getPeriodForBuilder(periodStr));
                pbuilder.setFooter("    |   Total scrobbles: "+ decimalFormat.format(totalPlaycount));
                Paginator p = pbuilder.setColor(Color.RED)
                        .setText("")
                        .build();
                message.editMessage("\u200B").queue();
                p.paginate(message, page);
            } catch (ArrayIndexOutOfBoundsException e) {
                message.editMessage("```❌ No tracks found for your account ❌```").queue();
            }
        });
    }


    public void topArtists(String discordID, int artistAmount, String periodStr, GuildMessageReceivedEvent event){
        AtomicInteger artistAmountTemp = new AtomicInteger(artistAmount);
        event.getChannel().sendMessage("```Loading data...```").queue(message -> {
            LastFmSQL sql = new LastFmSQL();
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            formatSymbols.setDecimalSeparator('.');
            formatSymbols.setGroupingSeparator(',');
            DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
            decimalFormat.setGroupingSize(3);
            decimalFormat.setGroupingUsed(true);
            String thumbnail = "";
            String username = sql.getUsername(discordID);
            LastFmTopArtistParser ta = new LastFmTopArtistParser(apikey, username, periodStr);
            String[][] artists = LastFmTopArtistParser.getResultArtists();
            try {

                if (artistAmountTemp.get() > artists.length) {
                    artistAmountTemp.set(artists.length);
                }

                String[] pages = new String[artistAmountTemp.get()];
                int pagetemp = 0;


                int totalPlayount = 0;
                for (String[] strings : artists) {
                    totalPlayount += Integer.parseInt(strings[3]);
                }
                for (int i = 0; i < artistAmountTemp.get(); i++) {
                    if (i == 0) {
                        pages[pagetemp] = "";
                    }

                    String rank = artists[i][0];
                    String artist = artists[i][1];
                    String artistlink = artists[i][2];
                    int playcount = Integer.parseInt(artists[i][3]);

                    /*if (artist.contains("*")) {
                        artist = artist.replace("*", "\\*");
                    }
                    if (artist.contains("*")) {
                        artist = artist.replace("*", "\\*");
                    }

                     */
                    if(i == 0){
                        if (playcount == 1) {
                            pages[pagetemp] += "🥇 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                        } else
                            pages[pagetemp] += "🥇 [" + artist+ "](" + artistlink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                    }
                    else if(i == 1){
                        if (playcount == 1) {
                            pages[pagetemp] += "🥈 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                        } else
                            pages[pagetemp] += "🥈 [" + artist+ "](" + artistlink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                    }
                    else if(i == 2){
                        if (playcount == 1) {
                            pages[pagetemp] += "🥉 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                        } else
                            pages[pagetemp] += "🥉 [" + artist+ "](" + artistlink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                    }
                    else {

                        if (playcount == 1) {
                            pages[pagetemp] += "#" + rank + " [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                        }
                        else pages[pagetemp] += "#" + rank + " [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                    }
                    if (pages[pagetemp].length() > 1800 && artists.length-1  != i) {
                        pagetemp++;
                        pages[pagetemp] = "";
                    }
                }


                String[] pagesReal = new String[pagetemp + 1];
                for (int j = 0; j <= pagetemp; j++) {
                    pagesReal[j] = "";
                    pagesReal[j] = pages[j];
                }


                String firstArtist = artists[0][1];
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
                        .setFinalAction(m -> {
                            try {
                                m.clearReactions().queue();
                            } catch (PermissionException e) {
                                m.delete().queue();
                            }
                        })
                        .setEventWaiter(waiter)
                        .setTimeout(2, TimeUnit.MINUTES);
                pbuilder.clearItems();
                pbuilder.addItems(pagesReal);
                pbuilder.setAuthorText("🎤" + username + "'s top artists");
                pbuilder.setAuthorURL("https://www.last.fm/user/" + username + "/library/artists?date_preset=" + periodforURL);
                pbuilder.setAuthorIconURL(event.getAuthor().getAvatarUrl());
                pbuilder.setThumbnail(thumbnail);
                pbuilder.setFooter("    |   Total scrobbles: "+ decimalFormat.format(totalPlayount));
                pbuilder.setTitle(getPeriodForBuilder(periodStr));
                Paginator p = pbuilder.setColor(Color.RED)
                        .setText("")
                        .build();
                message.editMessage("\u200B").queue();
                p.paginate(message, page);
            } catch (ArrayIndexOutOfBoundsException e) {
                message.editMessage("```❌ No artists found for your account ❌```").queue();
            }
        });
    }


    public void getProfile(String username, GuildMessageReceivedEvent event) {

        event.getChannel().sendMessage("```Loading data...```").queue(message -> {
            if(username != null) {
                LastFmProfileParser pp = new LastFmProfileParser(username, apikey);
                String[] profile = LastFmProfileParser.getStrings();

                String scrobbles = profile[0];
                String country = profile[1];
                String date = profile[2];
                String thumbnail = profile[3];
                String topArtist = profile[4];
                String topTrack = profile[5];
                String usernameURL = profile[7];


                EmbedBuilder account = new EmbedBuilder();
                account.setColor(0xFF0000);
                account.setAuthor("\uD83D\uDEC8 "+username + "'s Last.FM Account", usernameURL);
                account.setThumbnail(thumbnail);
                account.addField("Scrobbles", scrobbles, false);
                account.addField("Country", country, false);
                account.addField("Making my bot slower since", date, false);
                account.addField("Top artist", topArtist, false);
                account.addField("Top track", topTrack, true);
                message.editMessage("\u200B").queue();
                message.editMessage(account.build()).queue();
            }
            else message.editMessage(noUsernameMessage).queue();
        });
        /*
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

         */
    }

    public void getRecentTracks(String discordID, int trackamount, GuildMessageReceivedEvent event){
        AtomicInteger trackAmount = new AtomicInteger(trackamount);

        event.getChannel().sendMessage("```Loading data...```").queue(message -> {
            LastFmSQL sql = new LastFmSQL();
            DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
            formatSymbols.setDecimalSeparator('.');
            formatSymbols.setGroupingSeparator(',');
            DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
            decimalFormat.setGroupingSize(3);
            decimalFormat.setGroupingUsed(true);
            String thumbnail = "";
            String username = sql.getUsername(discordID);
            LastFmRecentTracksParser rt = new LastFmRecentTracksParser(apikey, username, trackamount);
            String[][] recentTracks = LastFmRecentTracksParser.getResultsRecents();
            if(trackAmount.get() > recentTracks.length){
                trackAmount.set(recentTracks.length);
            }
            try {

                String[] pages = new String[trackAmount.get()];
                int pagetemp = 0;


                int totalPlayount = 0;
                totalPlayount += Integer.parseInt(recentTracks[0][5]);

                for (int i = 0; i < trackAmount.get(); i++) {
                    if (i == 0) {
                        pages[pagetemp] = "";
                    }

                    String rank = recentTracks[i][0];
                    String artist = recentTracks[i][1];
                    String trackname = recentTracks[i][2];
                    String tracklink = recentTracks[i][3];
                    String timeAgo = recentTracks[i][4];
                    if (artist.contains("*")) {
                        artist = artist.replace("*", "\\*");
                    }
                    if (artist.contains("*")) {
                        artist = artist.replace("*", "\\*");
                    }

                    if(timeAgo.equalsIgnoreCase("now")){
                        pages[pagetemp] += "🎧 #" + rank + " " + artist + " - [" + trackname + "](" + tracklink + ") (" + timeAgo+ ") 🎧\n";
                    }
                    else {
                        pages[pagetemp] += "#" + rank + " " + artist + " - [" + trackname + "](" + tracklink + ") (" + timeAgo + ")\n";
                    }

                    if (pages[pagetemp].length() > 1800 && recentTracks.length-1 != i) {
                        pagetemp++;
                        pages[pagetemp] = "";
                    }
                }


                String[] pagesReal = new String[pagetemp + 1];
                for (int j = 0; j <= pagetemp; j++) {
                    pagesReal[j] = "";
                    pagesReal[j] = pages[j];
                }

                thumbnail = recentTracks[0][6];

                sql.closeConnection();



                int page = 1;
                //String periodforURL = checkPeriodforURL(periodStr);
                pbuilder = new Paginator.Builder()
                        .setColumns(1)
                        .setBulkSkipNumber(2000)
                        .setItemsPerPage(1)
                        .showPageNumbers(true)
                        .waitOnSinglePage(false)
                        .useNumberedItems(false)
                        .setFinalAction(m -> {
                            try {
                                m.clearReactions().queue();
                            } catch (PermissionException e) {
                                m.delete().queue();
                            }
                        })
                        .setEventWaiter(waiter)
                        .setTimeout(2, TimeUnit.MINUTES);
                pbuilder.clearItems();
                pbuilder.addItems(pagesReal);
                pbuilder.setAuthorText("💽 " + username + "'s recent tracks");
                pbuilder.setAuthorURL("https://www.last.fm/user/" + username);
                pbuilder.setAuthorIconURL(event.getAuthor().getAvatarUrl());
                pbuilder.setThumbnail(thumbnail);
                pbuilder.setFooter("    |   Total scrobbles: "+ decimalFormat.format(totalPlayount));
                pbuilder.setTitle("Recent tracks");//getPeriodForBuilder(periodStr));
                Paginator p = pbuilder.setColor(Color.RED)
                        .setText("")
                        .build();
                message.editMessage("\u200B").queue();
                p.paginate(message, page);
            } catch (ArrayIndexOutOfBoundsException e) {
                message.editMessage("```❌ No tracks found for your account ❌```").queue();
                e.printStackTrace();
            }


        });
    }

    public boolean checkIfUserExist(String username){
        User testuser = null;
        try{
            testuser = User.getInfo(username, apikey);
            return testuser != null;
        } catch (NullPointerException e){
            return false;
        }

    }

    public void deleteUsernameInSQL(String discordID, GuildMessageReceivedEvent event){
        LastFmSQL sql = new LastFmSQL();
        sql.deleteQuery(discordID, sql.getUsername(discordID));
        sql.closeConnection();
        event.getChannel().sendMessage("```Removed your Last.FM account ✅```").queue();

    }

    public String checkPeriodforURL(String period) {
        String periodforURL = "LAST_7_DAYS";
        if (period.equalsIgnoreCase("week") || period.equalsIgnoreCase("7day")) {
            periodforURL = "LAST_7_DAYS";
        } else if (period.equalsIgnoreCase("1month")) {
            periodforURL = "LAST_30_DAYS";
        } else if (period.equalsIgnoreCase("month")) {
            periodforURL = "LAST_90_DAYS";
        } else if (period.equalsIgnoreCase("6month")) {
            periodforURL = "LAST_180_DAYS";
        } else if (period.equalsIgnoreCase("12month")) {
            periodforURL = "LAST_365_DAYS";
        } else if (period.equalsIgnoreCase("overall")) {
            periodforURL = "ALL";
        }
        return periodforURL;
    }

    public String getPeriodForBuilder(String periodStr) {
        if (periodStr.equalsIgnoreCase("week") || periodStr.equalsIgnoreCase("7day")) {
            return "Last week";
        } else if (periodStr.equalsIgnoreCase("1month")) {
            return "Last month";
        } else if (periodStr.equalsIgnoreCase("3month")) {
            return "Last 3 months";
        } else if (periodStr.equalsIgnoreCase("6month")) {
            return "Last 6 months";
        } else if (periodStr.equalsIgnoreCase("12month")) {
            return "Last year";
        } else if (periodStr.equalsIgnoreCase("overall")) {
            return "Overall";
        } else return "Last week";
    }

    public boolean setUsernameInDatabase(String username) {
        String name = username;
        User testUser = null;
        try {
            testUser = User.getInfo(name, apikey);
            if (testUser != null) {
                setMessageTosend("```Linked last.FM username '" + getUsername() + "' with your discord account ✅```");
                return true;
            } else {
                setMessageTosend("```❌ Username '" + username + "' does not exist ❌```");
                return false;

            }

        } catch (Exception e) {
            setMessageTosend("```❌ Username '" + username + "' does not exist ❌```");
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
