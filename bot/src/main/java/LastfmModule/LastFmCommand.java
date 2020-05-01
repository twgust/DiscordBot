package LastfmModule;

import Commands.Command;
import Main.EventListener;
import MusicModule.MusicController;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import de.umass.lastfm.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.GenericMessageEvent;
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
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class LastFmCommand extends Command {
    private long start;
    private String failedToLoad = "```⚠ Failed to load, try again please. ⚠```";
    private static final String apikey = "c806a80470bbd773b00c2b46b3a1fd75";
    private String discordID;
    private String username;
    private String messageReceived;
    private String[] messageReceivedArr;
    private String messageTosend;
    private int maxTrackAmount = 10;
    private String periodStr;
    private User user;
    private EventWaiter waiter;
    private MusicController musicController;
    private Paginator.Builder pbuilder;
    private String noUsernameMessage = "```❌ You've not linked your lastfm username. Type " + EventListener.prefix + "fm set <username>. Type " + EventListener.prefix + "help for more help noob. ❌```";
    private String wrongFormatMessage = "```❌ Invalid format, try again. Type " + EventListener.prefix + "help for more help noob ❌```";

    public LastFmCommand(EventWaiter waiter, MusicController musicController) {
        this.waiter = waiter;
        this.musicController = musicController;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        setStart(System.nanoTime());
        setUsername(null);
        setDiscordID(event.getAuthor().getId());
        setMessageReceived(event.getMessage().getContentRaw());
        setMessageReceivedArr(getMessageReceived().split(" "));
        LastFmSQL sql1 = new LastFmSQL();

        if (getMessageReceivedArr().length == 1) {
            executeProfileSelf(sql1, event);
        } else if (getMessageReceivedArr().length == 2) {
            if (getMessageReceivedArr()[1].equalsIgnoreCase("tt") || getMessageReceivedArr()[1].equalsIgnoreCase("toptracks")) {
                topTracks(getDiscordID(), 10, "7day", event, sql1);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("ta") || getMessageReceivedArr()[1].equalsIgnoreCase("topartists")) {
                topArtists(getDiscordID(), 10, "7day", event, sql1);
            } else if (getMessageReceivedArr()[1].contains("<@!")) {
                executeProfileTagged(sql1, event);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("delete") || getMessageReceivedArr()[1].equalsIgnoreCase("del") || getMessageReceivedArr()[1].equalsIgnoreCase("remove")) {
                deleteUsernameInSQL(getDiscordID(), event, sql1);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("recent") || getMessageReceivedArr()[1].equalsIgnoreCase("rt")) {
                getRecentTracks(getDiscordID(), 10, event, sql1);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("nowplaying") || getMessageReceivedArr()[1].equalsIgnoreCase("np")) {
                getNowPlaying(getDiscordID(), event, sql1);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("chart")) {
                getChartAlbum(getDiscordID(), 3, "7day", event, sql1);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("youtube") || getMessageReceivedArr()[1].equalsIgnoreCase("yt")) {
                executeYoutubeSelf(sql1, event);
            } else {
                executeProfile(sql1, event);
            }

        } else if (getMessageReceivedArr().length == 3) {
            if (getMessageReceivedArr()[1].equalsIgnoreCase("set")) {
                setUsernameInDatabase(getMessageReceivedArr()[2], event, sql1);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("tt") || getMessageReceivedArr()[1].equalsIgnoreCase("toptracks")) {
                executeTopTracksNoAmount(sql1, event);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("ta") || getMessageReceivedArr()[1].equalsIgnoreCase("topartists")) {
                executeTopArtistNoAmount(sql1, event);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("recent") || getMessageReceivedArr()[1].equalsIgnoreCase("rt")) {
                executeRecentTracks(sql1, event);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("chart")) {
                executeChart(sql1, event, "7day");
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("youtube") || getMessageReceivedArr()[1].equalsIgnoreCase("yt")) {
                executeYouTubeUsername(sql1, event, getMessageReceivedArr()[2]);
            } else {
                sql1.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }

        } else if (getMessageReceivedArr().length == 4) {
            if (getMessageReceivedArr()[1].equalsIgnoreCase("tt") || getMessageReceivedArr()[1].equalsIgnoreCase("toptracks")) {
                executeTopTracks(sql1, event);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("ta") || getMessageReceivedArr()[1].equalsIgnoreCase("topartists")) {
                executeTopArtists(sql1,event);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("chart")) {
                executeChart(sql1, event, getMessageReceivedArr()[3]);
            } else if (getMessageReceivedArr()[1].equalsIgnoreCase("play")) {
                if(getMessageReceivedArr()[2].equalsIgnoreCase("tt") || getMessageReceivedArr()[2].equalsIgnoreCase("toptracks")) {
                    playMusicFromTopList(getDiscordID(), event, sql1, getMessageReceivedArr()[3]);
                }
            }

        } else {
            sql1.closeConnection();
            event.getChannel().sendMessage("Use correct format (HOLDER FOR UPCOMING SHIT POGU)").queue();
        }
    }

    public void topTracks(String discordID, int trackAmount, String periodStr, GuildMessageReceivedEvent event, LastFmSQL sql) {

        if (sql.checkQuery(getDiscordID())) {
            setPeriodStr("7day");

            AtomicInteger trackAmountTemp = new AtomicInteger(trackAmount);

            event.getChannel().sendMessage("```Loading data...```").queue(message -> {
                DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
                formatSymbols.setDecimalSeparator('.');
                formatSymbols.setGroupingSeparator(',');
                DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
                decimalFormat.setGroupingSize(3);
                decimalFormat.setGroupingUsed(true);
                String thumbnail = "";
                String username = sql.getUsername(discordID);
                sql.closeConnection();
                LastFmTopTracksParser tt = new LastFmTopTracksParser(apikey, username, periodStr);
                if (tt.isLoaded()) {
                    String[][] tracks = tt.getResultTracks();
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
                            if (i == 0) {
                                if (playcount == 1) {
                                    pages[pagetemp] += "🥇 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";

                                } else
                                    pages[pagetemp] += "🥇 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                            } else if (i == 1) {
                                if (playcount == 1) {
                                    pages[pagetemp] += "🥈 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                                } else
                                    pages[pagetemp] += "🥈 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                            } else if (i == 2) {
                                if (playcount == 1) {
                                    pages[pagetemp] += "🥉 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                                } else
                                    pages[pagetemp] += "🥉 " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                            } else {

                                if (playcount == 1) {
                                    pages[pagetemp] += "#" + rank + " " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                                } else
                                    pages[pagetemp] += "#" + rank + " " + artist + " - [" + track + "](" + tracklink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";

                            }

                            if (pages[pagetemp].length() > 1800 && tracks.length - 1 != i) {
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

                        } catch (Exception e) {
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
                        pbuilder.setFooter("    |   Total scrobbles: " + decimalFormat.format(totalPlaycount));
                        Paginator p = pbuilder.setColor(Color.RED)
                                .setText("")
                                .build();
                        message.editMessage("Response time: " + getResponseTime() + " seconds").queue();
                        p.paginate(message, page);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        message.editMessage("```❌ No tracks found for your account ❌```").queue();
                    }
                } else message.editMessage(failedToLoad).queue();
            });
        } else event.getChannel().sendMessage(noUsernameMessage).queue();
    }


    public void topArtists(String discordID, int artistAmount, String periodStr, GuildMessageReceivedEvent event, LastFmSQL sql) {
        if (sql.checkQuery(getDiscordID())) {
            setPeriodStr("7day");


            AtomicInteger artistAmountTemp = new AtomicInteger(artistAmount);
            event.getChannel().sendMessage("```Loading data...```").queue(message -> {
                DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
                formatSymbols.setDecimalSeparator('.');
                formatSymbols.setGroupingSeparator(',');
                DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
                decimalFormat.setGroupingSize(3);
                decimalFormat.setGroupingUsed(true);
                String thumbnail = "";
                String username = sql.getUsername(discordID);
                sql.closeConnection();
                LastFmTopArtistParser ta = new LastFmTopArtistParser(apikey, username, periodStr);
                if (ta.isLoaded()) {
                    String[][] artists = ta.getResultArtists();
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
                            if (i == 0) {
                                if (playcount == 1) {
                                    pages[pagetemp] += "🥇 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                                } else
                                    pages[pagetemp] += "🥇 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                            } else if (i == 1) {
                                if (playcount == 1) {
                                    pages[pagetemp] += "🥈 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                                } else
                                    pages[pagetemp] += "🥈 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                            } else if (i == 2) {
                                if (playcount == 1) {
                                    pages[pagetemp] += "🥉 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                                } else
                                    pages[pagetemp] += "🥉 [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                            } else {

                                if (playcount == 1) {
                                    pages[pagetemp] += "#" + rank + " [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " play)" + "\n";
                                } else
                                    pages[pagetemp] += "#" + rank + " [" + artist + "](" + artistlink + ") (" + decimalFormat.format(playcount) + " plays)" + "\n";
                            }
                            if (pages[pagetemp].length() > 1800 && artists.length - 1 != i) {
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

                        } catch (Exception e) {
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
                        pbuilder.setFooter("    |   Total scrobbles: " + decimalFormat.format(totalPlayount));
                        pbuilder.setTitle(getPeriodForBuilder(periodStr));
                        Paginator p = pbuilder.setColor(Color.RED)
                                .setText("")
                                .build();
                        message.editMessage("Response time: " + getResponseTime() + " seconds").queue();
                        p.paginate(message, page);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        message.editMessage("```❌ No artists found for your account ❌```").queue();
                    }
                } else message.editMessage(failedToLoad).queue();
            });
        } else event.getChannel().sendMessage(noUsernameMessage).queue();
    }


    public void getProfile(GuildMessageReceivedEvent event, LastFmSQL sql, String username) {

        if (sql.checkQuery(getDiscordID())) {
            //String username = sql.getUsername(getDiscordID());
            sql.closeConnection();
            event.getChannel().sendMessage("```Loading data...```").queue(message -> {
                if (username != null) {
                    LastFmProfileParser pp = new LastFmProfileParser(username, apikey);
                    if (pp.isLoaded()) {
                        String[] profile = pp.getProfile();

                        String scrobbles = profile[0];
                        String country = profile[1];
                        String date = profile[2];
                        String thumbnail = profile[3];
                        String topArtist = profile[4];
                        String topTrack = profile[5];
                        String usernameURL = profile[7];


                        EmbedBuilder account = new EmbedBuilder();
                        account.setColor(0xFF0000);
                        account.setAuthor("\uD83D\uDEC8 " + username + "'s Last.FM Account", usernameURL);
                        account.setThumbnail(thumbnail);
                        account.addField("Scrobbles", scrobbles, false);
                        account.addField("Country", country, false);
                        account.addField("Making my bot slower since", date, false);
                        account.addField("Top artist", topArtist, false);
                        account.addField("Top track", topTrack, true);
                        message.editMessage("\u200B").queue();
                        message.editMessage(account.build()).queue();
                    } else message.editMessage(failedToLoad).queue();

                } else message.editMessage(noUsernameMessage).queue();
            });
        } else event.getChannel().sendMessage(noUsernameMessage).queue();
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

    public void getRecentTracks(String discordID, int trackamount, GuildMessageReceivedEvent event, LastFmSQL sql) {
        if (sql.checkQuery(getDiscordID())) {

            AtomicInteger trackAmount = new AtomicInteger(trackamount);

            event.getChannel().sendMessage("```Loading data...```").queue(message -> {
                DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
                formatSymbols.setDecimalSeparator('.');
                formatSymbols.setGroupingSeparator(',');
                DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
                decimalFormat.setGroupingSize(3);
                decimalFormat.setGroupingUsed(true);
                String thumbnail = "";
                String username = sql.getUsername(discordID);
                sql.closeConnection();
                LastFmRecentTracksParser rt = new LastFmRecentTracksParser(apikey, username, trackamount);
                if (rt.isLoaded()) {
                    String[][] recentTracks = rt.getResultsRecents();
                    if (trackAmount.get() > recentTracks.length) {
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
                            if (trackname.contains("*")) {
                                trackname = trackname.replace("*", "\\*");
                            }

                            if (timeAgo.equalsIgnoreCase("now")) {
                                pages[pagetemp] += "🎧 #" + rank + " " + artist + " - [" + trackname + "](" + tracklink + ") (" + timeAgo + ") 🎧\n";
                            } else {
                                pages[pagetemp] += "#" + rank + " " + artist + " - [" + trackname + "](" + tracklink + ") (" + timeAgo + ")\n";
                            }

                            if (pages[pagetemp].length() > 1800 && recentTracks.length - 1 != i) {
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
                        pbuilder.setFooter("    |   Total scrobbles: " + decimalFormat.format(totalPlayount));
                        pbuilder.setTitle("Recent tracks");//getPeriodForBuilder(periodStr));
                        Paginator p = pbuilder.setColor(Color.RED)
                                .setText("")
                                .build();
                        message.editMessage("Response time: " + getResponseTime() + " seconds").queue();
                        p.paginate(message, page);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        message.editMessage("```❌ No tracks found for your account ❌```").queue();
                        e.printStackTrace();
                    }
                } else message.editMessage(failedToLoad).queue();


            });
        } else {
            event.getChannel().sendMessage(noUsernameMessage).queue();
            sql.closeConnection();
        }
    }

    public void getNowPlaying(String discordID, GuildMessageReceivedEvent event, LastFmSQL sql) {

        if (sql.checkQuery(getDiscordID())) {
            event.getChannel().sendMessage("```Loading data...```").queue(message -> {
                DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
                formatSymbols.setDecimalSeparator('.');
                formatSymbols.setGroupingSeparator(',');
                DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
                decimalFormat.setGroupingSize(3);
                decimalFormat.setGroupingUsed(true);

                String username = sql.getUsername(discordID);
                sql.closeConnection();
                LastFmNowPlayingParser np = new LastFmNowPlayingParser(apikey, username);
                if (np.isLoaded()) {


                    String[][] nowPlayingInfo = np.getNowplayingInfo();
                    if (!nowPlayingInfo[0][0].equalsIgnoreCase("not loaded")) {
                        //String[][] embedInfo = new String[2][8];
                        String fieldPlaying = "Last played (" + nowPlayingInfo[0][4] + ")";
                        String userLink = "https://www.last.fm/user/" + username;

                        for (int i = 0; i < nowPlayingInfo.length; i++) {

                            if (nowPlayingInfo[i][1].contains("*")) {
                                nowPlayingInfo[i][1] = nowPlayingInfo[i][2].replace("*", "\\*");
                            }
                            if (nowPlayingInfo[i][2].contains("*")) {
                                nowPlayingInfo[i][2] = nowPlayingInfo[i][2].replace("*", "\\*");
                            }

                        }
                        String authorText = "'s last played";
                        if (nowPlayingInfo[0][4].equalsIgnoreCase("now")) {
                            fieldPlaying = "Now playing";
                            authorText = "'s current track";
                        }
                        String artistName = nowPlayingInfo[0][1];
                        String trackName = nowPlayingInfo[0][2];
                        String trackLink = nowPlayingInfo[0][3];
                        String totalScrobbles = nowPlayingInfo[0][5];
                        String thumbnail = nowPlayingInfo[0][6];
                        String trackScrobbles = nowPlayingInfo[0][7];
                        EmbedBuilder nowPlaying = new EmbedBuilder();

                        if (nowPlayingInfo.length != 1) {
                            String artistNamePrevious = nowPlayingInfo[1][1];
                            String trackNamePrevious = nowPlayingInfo[1][2];
                            String trackLinkPrevious = nowPlayingInfo[1][3];
                            message.editMessage("\u200B").queue();
                            nowPlaying.setAuthor("🎧 " + username + authorText, userLink, event.getAuthor().getAvatarUrl());
                            nowPlaying.addField(fieldPlaying, "[" + trackName + "](" + trackLink + ") - " + artistName, false);
                            nowPlaying.addField("Listened to previously", "[" + trackNamePrevious + "](" + trackLinkPrevious + ") - " + artistNamePrevious, false);

                        } else {
                            message.editMessage("\u200B").queue();
                            nowPlaying.setAuthor("🎧 " + username + authorText, userLink, event.getAuthor().getAvatarUrl());
                            nowPlaying.addField(fieldPlaying, "[" + trackName + "](" + trackLink + ") - " + artistName, false);

                        }
                        nowPlaying.setThumbnail(thumbnail);
                        if (trackScrobbles.equalsIgnoreCase("Failed to load")) {
                            nowPlaying.setFooter("Total trackplays: " + trackScrobbles + "      |       Total scrobbles: " + totalScrobbles);
                        } else
                            nowPlaying.setFooter("Total trackplays: " + decimalFormat.format(Integer.parseInt(trackScrobbles)) + "      |       Total scrobbles: " + decimalFormat.format(Integer.parseInt(totalScrobbles)));
                        nowPlaying.setColor(0xFF0000);

                        message.editMessage(nowPlaying.build()).queue();
                    } else message.editMessage("```❌ No tracks found for your account ❌```").queue();
                } else message.editMessage(failedToLoad).queue();
            });
        } else {
            event.getChannel().sendMessage(noUsernameMessage).queue();
            sql.closeConnection();
        }
    }

    public double getResponseTime() {
        long end = System.nanoTime();
        long elapsedTime = end - start;
        double elapsedTimeDouble = (double) elapsedTime / 1_000_000_000;
        BigDecimal bd = BigDecimal.valueOf(elapsedTimeDouble);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void getChartAlbum(String discordID, int size, String period, GuildMessageReceivedEvent event, LastFmSQL sql) {
        if (sql.checkQuery(getDiscordID())) {
            /*
            if (getMessageReceivedArr()[2].contains("x")) {
                String size = getMessageReceivedArr()[2];
                //sql.closeConnection();
                //getChartAlbum(getDiscordID(), size, "7day", event);


                String[] arraySize = size.split("x");
                //KANSKE EN TRY CATCH IFALL FEL FORMAT?
                int x = 0;
                int y = 0;
                try {
                    x = Integer.parseInt(arraySize[0]);
                    y = Integer.parseInt(arraySize[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    event.getChannel().sendMessage(wrongFormatMessage).queue();
                    return;
                }

                int result = (int) Math.round(Math.sqrt(x * y));
                if (result > 10) {
                    result = 10;
                }

             */
            String amountOfAlbums = Integer.toString(size * size);

            event.getChannel().sendMessage("```Loading data...```").queue(message -> {
                String username = sql.getUsername(discordID);
                sql.closeConnection();
                LastFmTopAlbumsParserChart ap = new LastFmTopAlbumsParserChart(apikey, username, getPeriodForAPICall(period), amountOfAlbums);
                if (ap.isLoaded()) {
                    String[][] albumsInfo = ap.getTopAlbums();
                    if (albumsInfo.length > 0) {
                        int amountAlbums = Integer.parseInt(amountOfAlbums);
                        if (amountAlbums > albumsInfo.length) {
                            amountAlbums = albumsInfo.length;
                        }
                        int rowColSize = (int) Math.round(Math.sqrt(amountAlbums));
                        int rowSize = rowColSize;
                        if (albumsInfo.length > rowColSize * rowColSize && rowColSize < 10) {
                            rowSize++;
                        }


                        int dimensionHeight = rowColSize * 300;
                        int dimensionWidth = rowSize * 300;
                        LastFmTopAlbumHTML albumHTML = new LastFmTopAlbumHTML();
                        albumHTML.createHTMLfile(albumsInfo, rowColSize, rowSize);
                        albumHTML.createJSFile(dimensionHeight, dimensionWidth);
                        albumHTML.runJSFile();
                        message.delete().queue();
                        event.getChannel().sendMessage(username + "'s top albums " + getPeriodForBuilder(getPeriodForAPICall(period))).addFile(new File("testimages/image.jpg")).queue();
                    } else message.editMessage("```❌ No albums found for your account ❌```").queue();
                } else message.editMessage("```Failed to load, try again please```").queue();
            });
            /*} else {
                sql.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }

             */
        } else {
            sql.closeConnection();
            event.getChannel().sendMessage(noUsernameMessage).queue();
        }
    }

    public void getYoutubeLink(String username, GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("```Loading data```").queue(message -> {

            LastFmYoutube fmYoutube = new LastFmYoutube(apikey, username);
            if (fmYoutube.isLoaded()) {
                String[] ytInfo = fmYoutube.getYtLink();
                String ytLink = ytInfo[0];
                String lastPlayed = ytInfo[1];
                if (!lastPlayed.equalsIgnoreCase("noplays")) {
                    message.editMessage(lastPlayed + ytLink).queue();
                } else message.editMessage("```No recent tracks found for the account```").queue();

            } else message.editMessage("```Failed to load, please try again.```").queue();

        });
    }

    public void playMusicFromTopList(String discordID, GuildMessageReceivedEvent event, LastFmSQL sql, String period){
        if (sql.checkQuery(discordID)){
            event.getChannel().sendMessage("```Loading music...```").queue(message -> {
                LastFmTopTracksParserMusic ttmusic = new LastFmTopTracksParserMusic("c806a80470bbd773b00c2b46b3a1fd75", sql.getUsername(discordID), getPeriodForAPICall(period), 5);
                sql.closeConnection();
                if (ttmusic.isLoaded()) {
                    String[][] tracks = ttmusic.getResultTracks();
                    System.out.println(Arrays.deepToString(tracks));
                    int length = tracks.length;
                    int counter = 1;
                    for (int i = 0; i < length; i++){
                        musicController.loadMusic(tracks[i][2], event.getMember(), event);
                        message.editMessage("Loaded " + counter + " song").queue();
                        counter++;
                        if(counter == length+1){
                            message.editMessage("Finished loading!").queue();
                            break;
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
                else {
                    event.getChannel().sendMessage(failedToLoad).queue();
                    sql.closeConnection();
                }

            });

        }
        else {
            event.getChannel().sendMessage(noUsernameMessage).queue();
        }

    }

    public boolean checkIfUserExist(String username) {
        User testuser = null;
        try {
            testuser = User.getInfo(username, apikey);
            return testuser != null;
        } catch (NullPointerException e) {
            return false;
        }

    }

    public void executeTopTracksNoAmount(LastFmSQL sql, GuildMessageReceivedEvent event) {

        if (sql.checkQuery(getDiscordID())) {
            if (getMessageReceivedArr()[2].equalsIgnoreCase("w") || getMessageReceivedArr()[2].equalsIgnoreCase("week")) {
                setPeriodStr("7day");
                //sql.closeConnection();
                topTracks(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("m") || getMessageReceivedArr()[2].equalsIgnoreCase("month")) {
                setPeriodStr("1month");
                //sql.closeConnection();
                topTracks(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("3m") || getMessageReceivedArr()[2].equalsIgnoreCase("3month")) {
                setPeriodStr("3month");
                //sql.closeConnection();
                topTracks(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("6m") || getMessageReceivedArr()[2].equalsIgnoreCase("6month")) {
                setPeriodStr("6month");
                //sql.closeConnection();
                topTracks(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("y") || getMessageReceivedArr()[2].equalsIgnoreCase("year") || getMessageReceivedArr()[2].equalsIgnoreCase("12m") || getMessageReceivedArr()[2].equalsIgnoreCase("12month")) {
                setPeriodStr("12month");
                //sql.closeConnection();
                topTracks(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("all") || getMessageReceivedArr()[2].equalsIgnoreCase("overall") || getMessageReceivedArr()[2].equalsIgnoreCase("at") || getMessageReceivedArr()[2].equalsIgnoreCase("alltime")) {
                setPeriodStr("overall");
                //sql.closeConnection();
                topTracks(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else {
                setPeriodStr("7day");
                try {
                    setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[2]));
                    //sql.closeConnection();
                    topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
                } catch (NumberFormatException e) {
                    setMaxTrackAmount(10);
                    sql.closeConnection();
                    event.getChannel().sendMessage(wrongFormatMessage).queue();
                }
            }
        } else {
            sql.closeConnection();
            event.getChannel().sendMessage(noUsernameMessage).queue();
        }
    }


    public void executeTopArtistNoAmount(LastFmSQL sql, GuildMessageReceivedEvent event) {
        if (sql.checkQuery(getDiscordID())) {
            if (getMessageReceivedArr()[2].equalsIgnoreCase("w") || getMessageReceivedArr()[2].equalsIgnoreCase("week")) {
                setPeriodStr("7day");
                //sql.closeConnection();
                topArtists(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("m") || getMessageReceivedArr()[2].equalsIgnoreCase("month")) {
                setPeriodStr("1month");
                //sql.closeConnection();
                topArtists(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("3m") || getMessageReceivedArr()[2].equalsIgnoreCase("3month")) {
                setPeriodStr("3month");
                //sql.closeConnection();
                topArtists(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("6m") || getMessageReceivedArr()[2].equalsIgnoreCase("6month")) {
                setPeriodStr("6month");
                //sql.closeConnection();
                topArtists(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("y") || getMessageReceivedArr()[2].equalsIgnoreCase("year") || getMessageReceivedArr()[2].equalsIgnoreCase("12m") || getMessageReceivedArr()[2].equalsIgnoreCase("12month")) {
                setPeriodStr("12month");
                //sql.closeConnection();
                topArtists(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else if (getMessageReceivedArr()[2].equalsIgnoreCase("all") || getMessageReceivedArr()[2].equalsIgnoreCase("overall") || getMessageReceivedArr()[2].equalsIgnoreCase("at") || getMessageReceivedArr()[2].equalsIgnoreCase("alltime")) {
                setPeriodStr("overall");
                //sql.closeConnection();
                topArtists(getDiscordID(), 10, getPeriodStr(), event, sql);
            } else {
                setPeriodStr("7day");
                try {
                    setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[2]));
                    //sql.closeConnection();
                    topArtists(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
                } catch (NumberFormatException e) {
                    setMaxTrackAmount(10);
                    sql.closeConnection();
                    event.getChannel().sendMessage(wrongFormatMessage).queue();
                }
            }
        } else {
            sql.closeConnection();
            event.getChannel().sendMessage(noUsernameMessage).queue();
        }
    }

    public void executeTopTracks(LastFmSQL sql, GuildMessageReceivedEvent event) {
        if (getMessageReceivedArr()[2].equalsIgnoreCase("w") || getMessageReceivedArr()[2].equalsIgnoreCase("week")) {
            setPeriodStr("7day");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                sql.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("m") || getMessageReceivedArr()[2].equalsIgnoreCase("month")) {
            setPeriodStr("1month");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                sql.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("3m") || getMessageReceivedArr()[2].equalsIgnoreCase("3month")) {
            setPeriodStr("3month");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                sql.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("6m") || getMessageReceivedArr()[2].equalsIgnoreCase("6month")) {
            setPeriodStr("6month");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                sql.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("y") || getMessageReceivedArr()[2].equalsIgnoreCase("year") || getMessageReceivedArr()[2].equalsIgnoreCase("12m") || getMessageReceivedArr()[2].equalsIgnoreCase("12month")) {
            setPeriodStr("12month");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                event.getChannel().sendMessage(wrongFormatMessage).queue();
                sql.closeConnection();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("all") || getMessageReceivedArr()[2].equalsIgnoreCase("overall") || getMessageReceivedArr()[2].equalsIgnoreCase("at") || getMessageReceivedArr()[2].equalsIgnoreCase("alltime")) {
            setPeriodStr("overall");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topTracks(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                sql.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }
        } else {
            event.getChannel().sendMessage(wrongFormatMessage).queue();
            sql.closeConnection();
        }
    }

    public void executeTopArtists(LastFmSQL sql, GuildMessageReceivedEvent event) {
        if (getMessageReceivedArr()[2].equalsIgnoreCase("w") || getMessageReceivedArr()[2].equalsIgnoreCase("week")) {
            setPeriodStr("7day");
            try {
                //sql.closeConnection();
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                topArtists(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);

            } catch (NumberFormatException e) {
                sql.closeConnection();
                setMaxTrackAmount(10);
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("m") || getMessageReceivedArr()[2].equalsIgnoreCase("month")) {
            setPeriodStr("1month");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topArtists(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                event.getChannel().sendMessage(wrongFormatMessage).queue();
                sql.closeConnection();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("3m") || getMessageReceivedArr()[2].equalsIgnoreCase("3month")) {
            setPeriodStr("3month");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topArtists(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                event.getChannel().sendMessage(wrongFormatMessage).queue();
                sql.closeConnection();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("6m") || getMessageReceivedArr()[2].equalsIgnoreCase("6month")) {
            setPeriodStr("6month");
            try {
                //sql.closeConnection();
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                topArtists(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);

            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                event.getChannel().sendMessage(wrongFormatMessage).queue();
                sql.closeConnection();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("y") || getMessageReceivedArr()[2].equalsIgnoreCase("year") || getMessageReceivedArr()[2].equalsIgnoreCase("12m") || getMessageReceivedArr()[2].equalsIgnoreCase("12month")) {
            setPeriodStr("12month");
            try {
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                //sql.closeConnection();
                topArtists(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);
            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                sql.closeConnection();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
            }
        } else if (getMessageReceivedArr()[2].equalsIgnoreCase("all") || getMessageReceivedArr()[2].equalsIgnoreCase("overall") || getMessageReceivedArr()[2].equalsIgnoreCase("at") || getMessageReceivedArr()[2].equalsIgnoreCase("alltime")) {
            setPeriodStr("overall");
            try {
                //sql.closeConnection();
                setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[3]));
                topArtists(getDiscordID(), getMaxTrackAmount(), getPeriodStr(), event, sql);

            } catch (NumberFormatException e) {
                setMaxTrackAmount(10);
                event.getChannel().sendMessage(wrongFormatMessage).queue();
                sql.closeConnection();
            }
        } else {
            event.getChannel().sendMessage(wrongFormatMessage).queue();
            sql.closeConnection();
        }
    }

    public void executeProfileTagged(LastFmSQL sql, GuildMessageReceivedEvent event) {
        net.dv8tion.jda.api.entities.User user = event.getMessage().getMentionedUsers().get(0);
        if (sql.checkQuery(user.getId())) {
            setDiscordID(user.getId());
            getProfile(event, sql, sql.getUsername(getDiscordID()));
        } else {
            setMessageTosend("```❌ No username linked to discord account. ❌```");
            event.getChannel().sendMessage(getMessageTosend()).queue();
            sql.closeConnection();
        }
    }

    public void executeProfile(LastFmSQL sql, GuildMessageReceivedEvent event) {
        if (checkIfUserExist(getMessageReceivedArr()[1])) {
            //sql.closeConnection();
            getProfile(event, sql, getMessageReceivedArr()[1]);
        } else {
            event.getChannel().sendMessage("```❌ Username '" + getMessageReceivedArr()[1] + "' does not exist ❌```").queue();
            sql.closeConnection();
        }
    }

    public void executeProfileSelf(LastFmSQL sql, GuildMessageReceivedEvent event) {
        if (sql.checkQuery(getDiscordID())) {
            getProfile(event, sql, sql.getUsername(getDiscordID()));
        } else {
            event.getChannel().sendMessage(noUsernameMessage).queue();
            sql.closeConnection();
        }
    }

    public void executeRecentTracks(LastFmSQL sql, GuildMessageReceivedEvent event) {
        try {
            setMaxTrackAmount(Integer.parseInt(getMessageReceivedArr()[2]));
            getRecentTracks(getDiscordID(), getMaxTrackAmount(), event, sql);
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(wrongFormatMessage).queue();
        }
    }

    public void executeChart(LastFmSQL sql, GuildMessageReceivedEvent event, String period) {

        if (getMessageReceivedArr()[2].contains("x")) {
            String size = getMessageReceivedArr()[2];
            String[] arraySize = size.split("x");
            //KANSKE EN TRY CATCH IFALL FEL FORMAT?
            int x = 0;
            int y = 0;
            try {
                x = Integer.parseInt(arraySize[0]);
                y = Integer.parseInt(arraySize[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                event.getChannel().sendMessage(wrongFormatMessage).queue();
                return;
            }

            int result = (int) Math.round(Math.sqrt(x * y));
            if (result > 10) {
                result = 10;
            }
            getChartAlbum(getDiscordID(), result, period, event, sql);
        } else {
            sql.closeConnection();
            event.getChannel().sendMessage(wrongFormatMessage).queue();
        }
    }

    public void executeYoutubeSelf(LastFmSQL sql, GuildMessageReceivedEvent event) {
        if (sql.checkQuery(getDiscordID())) {
            String user = sql.getUsername(getDiscordID());
            sql.closeConnection();
            getYoutubeLink(user, event);
        } else {
            sql.closeConnection();
            event.getChannel().sendMessage(noUsernameMessage).queue();
        }
    }

    public void executeYouTubeUsername(LastFmSQL sql, GuildMessageReceivedEvent event, String user) {
        //String user = getMessageReceivedArr()[2];
        System.out.println(user);
        if (user.contains("<@!")) {
            String userID = event.getMessage().getMentionedUsers().get(0).getId();
            if (sql.checkQuery(userID)) {
                user = sql.getUsername(userID);
                sql.closeConnection();
                getYoutubeLink(user, event);
            } else {
                sql.closeConnection();
                event.getChannel().sendMessage(noUsernameMessage).queue();
            }
        } else {
            if (checkIfUserExist(user)) {
                sql.closeConnection();
                getYoutubeLink(user, event);
            } else {
                sql.closeConnection();
                event.getChannel().sendMessage("```❌ Username '" + getMessageReceivedArr()[1] + "' does not exist ❌```").queue();
            }
        }
    }


    public void deleteUsernameInSQL(String discordID, GuildMessageReceivedEvent event, LastFmSQL sql) {
        if (sql.checkQuery(getDiscordID())) {
            sql.deleteQuery(discordID);
            sql.closeConnection();
            event.getChannel().sendMessage("```Removed your Last.FM account ✅```").queue();
        } else {
            event.getChannel().sendMessage(noUsernameMessage).queue();
            sql.closeConnection();
        }
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

    public String getPeriodForAPICall(String period) {
        String periodAPI = "7day";
        if (period.equalsIgnoreCase("week") || period.equalsIgnoreCase("7day") || period.equalsIgnoreCase("w") || period.equalsIgnoreCase("7days")) {
            periodAPI = "7day";
        } else if (period.equalsIgnoreCase("1month") || period.equalsIgnoreCase("m") || period.equalsIgnoreCase("month")) {
            periodAPI = "1month";
        } else if (period.equalsIgnoreCase("3month") || period.equalsIgnoreCase("3m")) {
            periodAPI = "3month";
        } else if (period.equalsIgnoreCase("6month") || period.equalsIgnoreCase("6m")) {
            periodAPI = "6month";
        } else if (period.equalsIgnoreCase("12month") || period.equalsIgnoreCase("12m") || period.equalsIgnoreCase("year") || period.equalsIgnoreCase("y")) {
            periodAPI = "12month";
        } else if (period.equalsIgnoreCase("overall") || period.equalsIgnoreCase("alltime") || period.equalsIgnoreCase("at")) {
            periodAPI = "overall";
        }
        return periodAPI;

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

    public boolean setUsernameInDatabase(String username, GuildMessageReceivedEvent event, LastFmSQL sql) {

        boolean success = false;
        User testUser = null;
        try {
            testUser = User.getInfo(username, apikey);
            if (testUser != null) {
                setUsername(getMessageReceivedArr()[2]);
                sql.setUsername(getDiscordID(), getUsername());
                sql.closeConnection();
                setMessageTosend("```Linked last.FM username '" + getUsername() + "' with your discord account ✅```");
                event.getChannel().sendMessage(messageTosend).queue();
                success = true;
                return true;
            } else {
                setMessageTosend("```❌ Username '" + username + "' does not exist ❌```");
                event.getChannel().sendMessage(messageTosend).queue();
                success = false;
                return false;
            }

        } catch (Exception e) {
            setMessageTosend("```❌ Username '" + username + "' does not exist ❌```");
            event.getChannel().sendMessage(messageTosend).queue();
            success = false;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public String getNoUsernameMessage() {
        return noUsernameMessage;
    }

    public void setNoUsernameMessage(String noUsernameMessage) {
        this.noUsernameMessage = noUsernameMessage;
    }
}
