package LastfmModule;

import Commands.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Menu;
import de.umass.lastfm.*;
import de.umass.lastfm.User;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.channel.voice.update.VoiceChannelUpdateUserLimitEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.internal.utils.PermissionUtil;
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
import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class LastFmCommandOld extends Command {
    private Map<String, String> userMap = new HashMap<>();
    private User user;
    private String userId;
    private String userAcc;
    private String apikey = "c806a80470bbd773b00c2b46b3a1fd75";
    private String recievedMessage;
    private String[] arrMessage;
    private String noUserNameMessage;
    private EmbedBuilder embedMessage;
    private String messageTosend;
    private int maxTrackAmount = 10;
    private Period period;
    private MessageEmbed messageEmbed;
    private String[] pages;
    private int page = 0;


    @Override
    public void execute(GuildMessageReceivedEvent event) {
        //readTextFileHashMap();
        Caller.getInstance().setUserAgent("tst");
        setApikey("c806a80470bbd773b00c2b46b3a1fd75");
        setUserAcc(null);
        setUserId(event.getAuthor().getId());
        setRecievedMessage(event.getMessage().getContentRaw());
        setArrMessage(getRecievedMessage().split(" "));
        setNoUserNameMessage("You have not linked a username with your discord account");

        LastFmSQL sql1 = new LastFmSQL();
        if (getArrMessage().length == 1) {

            if (sql1.checkQuery(getUserId())) {
                //sql1.listUsers();
                getProfile(userId);
                sql1.closeConnection();
                event.getChannel().sendMessage(getEmbedMessage().build()).queue();
            } else event.getChannel().sendMessage(getNoUserNameMessage()).queue();
        } else if (getArrMessage()[1].equalsIgnoreCase("set")) {
            LastFmSQL sql2 = new LastFmSQL();
            setLinkedAccount();
            sql2.setUsername(getUserId(), getUserAcc());
            sql2.closeConnection();
            event.getChannel().sendMessage(messageTosend).queue();
        } else if (getArrMessage()[1].equalsIgnoreCase("tt") || getArrMessage()[1].equalsIgnoreCase("toptracks")) {
            if (sql1.checkQuery(getUserId())) {
                if (getArrMessage().length == 2 || getArrMessage()[2].equalsIgnoreCase("week") || getArrMessage()[2].equalsIgnoreCase("w")) {
                    setPeriod(Period.WEEK);
                    if (getArrMessage().length == 2 || getArrMessage().length == 3) {

                        topTracks(getUserId(), 10, getPeriod());
                        sql1.closeConnection();
                        event.getChannel().sendMessage(getEmbedMessage().build()).queue();
                    } else if (getArrMessage().length == 4) {
                        try {
                            setMaxTrackAmount(Integer.parseInt(getArrMessage()[3]));
                        } catch (NumberFormatException e) {
                            setMaxTrackAmount(10);
                        }
                        sql1.closeConnection();
                        topTracks(getUserId(), getMaxTrackAmount(), getPeriod());

                        event.getChannel().sendMessage(getEmbedMessage().build()).queue();
                    }
                } else if (getArrMessage().length == 3 && !getArrMessage()[2].equalsIgnoreCase("week") && !getArrMessage()[2].equalsIgnoreCase("w")) {
                    setPeriod(Period.WEEK);
                    try {
                        setMaxTrackAmount(Integer.parseInt(getArrMessage()[2]));
                    } catch (NumberFormatException e) {
                        setMaxTrackAmount(10);
                        setMessageTosend("Invalid format, try again");
                        sql1.closeConnection();
                        event.getChannel().sendMessage(getMessageTosend()).queue();
                    }
                    sql1.closeConnection();
                    topTracks(getUserId(), getMaxTrackAmount(), getPeriod());
                    if (getMaxTrackAmount() > 20) {

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

                    } else {
                        sql1.closeConnection();
                        event.getChannel().sendMessage(getEmbedMessage().build()).queue();
                    }

                } else {
                    sql1.closeConnection();
                    topTracks(getUserId(), 10, getPeriod());
                    event.getChannel().sendMessage(getEmbedMessage().build()).queue();
                }

            } else event.getChannel().sendMessage(noUserNameMessage).queue();
            sql1.closeConnection();
        } else if (getArrMessage()[1].equalsIgnoreCase("delete") || getArrMessage()[1].equalsIgnoreCase("del") || getArrMessage()[1].equalsIgnoreCase("remove")) {
            if (checkIfInHashmap(getUserId())) {
                System.out.println(getUserId());
                deleteLinkedAccount(getUserId());
                event.getChannel().sendMessage(messageTosend).queue();
            } else event.getChannel().sendMessage(getNoUserNameMessage()).queue();
        }
        //System.out.println(getPages()[0]);
    }

    public boolean checkIfInHashmap(String discordId) {
        return userMap.containsKey(discordId);
    }

    public void topTracks(String userId, int maxTracks, Period period) {
        LastFmSQL sql2 = new LastFmSQL();
        setUser(User.getInfo((sql2.getUsername(userId)), getApikey()));
        Collection<Track> topTracks = User.getTopTracks(sql2.getUsername(userId), period, apikey);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
        decimalFormat.setGroupingSize(3);
        decimalFormat.setGroupingUsed(true);

        EmbedBuilder topTracksBuilder = new EmbedBuilder();
        String thumbnail = "";
        LinkedList<Track> tracks = new LinkedList<>(topTracks);
        Collection<Track> topTracksValues = User.getTopTracks(sql2.getUsername(userId), Period.WEEK, apikey);
        System.out.println(topTracksValues.iterator().next());
        System.out.println(tracks);


        try {
            thumbnail = tracks.get(0).getImageURL(ImageSize.MEDIUM);
            if (thumbnail.equalsIgnoreCase("https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png")) {
                Document doc = Jsoup.connect("https://www.last.fm/music/" + tracks.get(0).getArtist() + "/+images").userAgent("Chrome").get();
                Elements images = doc.getElementsByClass("image-list-item-wrapper");
                String imagelink = images.get(0).getElementsByTag("img").toString();
                imagelink = imagelink.replace("/avatar170s/", "/300x300/");
                imagelink += "</img>";

                InputSource is = new InputSource(new StringReader(imagelink));
                DOMParser dp = new DOMParser();
                dp.parse(is);
                org.w3c.dom.Document document = dp.getDocument();
                NodeList nl = document.getElementsByTagName("img");
                Node n = nl.item(0);
                NamedNodeMap nnm = n.getAttributes();
                thumbnail = nnm.getNamedItem("src").getFirstChild().getTextContent();
            } else System.out.println("Image is not the defaulted one");
        } catch (NullPointerException | IOException | SAXException e) {
            thumbnail = "https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png";
            e.printStackTrace();
        }

        sql2.getUsername(userId);

        topTracksBuilder.setAuthor("🎵 " +sql2.getUsername(userId) + "'s Top tracks", user.getUrl());
        sql2.closeConnection();
        topTracksBuilder.setThumbnail(thumbnail);
        topTracksBuilder.setTitle("Last week top " + maxTracks);
        topTracksBuilder.setColor(0xFF0000);

        int totalPages = 1;
        int counter = 1;
        int maxTrackAmountTemp = maxTracks - 1;
        if (maxTrackAmountTemp >= 19) {
            maxTrackAmountTemp = 19;
        }

        setPage(0);
        setPages(new String[(int) (Math.ceil(((float) tracks.size()) / 20))]);
        String pageInformation = "";
        String pageInformationDB = "";
        for (int i = 0; i <= tracks.size() - 1; i++) {
            String trackName = tracks.get(i).getName();
            if (trackName.contains("*")) {
                trackName = trackName.replace("*", "\\*");
            }
            String artistName = tracks.get(i).getArtist();
            if (artistName.contains("*")) {
                artistName = artistName.replace("*", "\\*");
            }
            String trackLink = tracks.get(i).getUrl();
            int playedCount = tracks.get(i).getPlaycount();
            pageInformation = counter + ". " + artistName + " - [" + trackName + "](" + trackLink + ") (" + decimalFormat.format(playedCount) + " plays)" + "\n";
            pageInformationDB += counter + ". " + artistName + " - [" + trackName + "](" + trackLink + ") (" + decimalFormat.format(playedCount) + " plays)" + "\n";
            //topTracksBuilder.appendDescription(counter + ". " + artistName +  " - [" + trackName + "](" + trackLink + ") (" + decimalFormat.format(playedCount) + " plays)" + "\n");
            counter++;
            //pages = new String[(int)(Math.ceil(((float)tracks.size() )/ 20))+1];
            if (i % 20 == 0 && i != 0) {
                setPage(getPage() + 1);
                totalPages++;
            }

            if (i % 20 == 0) {
                getPages()[getPage()] = "";
            }
            getPages()[getPage()] += pageInformation;
        }
        System.out.println(pageInformationDB);
        LastFmSQL sql = new LastFmSQL();
        sql.updateTopTracks(sql.getUsername(userId), pageInformationDB, "week");
        sql.closeConnection();


        //System.out.println(getPages()[0]);
        //System.out.println(getPages()[1]);
        //System.out.println(getPages()[2]);
        topTracksBuilder.appendDescription(getPages()[0]);
        topTracksBuilder.setFooter("Page 1 of " + totalPages);

        setEmbedMessage(topTracksBuilder);

        //else setMessageTosend(noUserNameMessage);

    }

    public void setLinkedAccount() {
        setUserAcc(getArrMessage()[2]);
        User testUser = null;
        try {
            testUser = User.getInfo(getUserAcc(), getApikey());
        } catch (Exception e) {
            setMessageTosend("Something wrong with API callbacks");
        }
        try {
            testUser.getName();
            if (checkIfInHashmap(getUserId())) {
                userMap.replace(getUserId(), getUserAcc());
            } else put(getUserId(), getUserAcc());
            setMessageTosend("Linked your account with provided username");
            /*final String outPutFilePath = "D:/lastfmaccounts.txt";
            File file = new File(outPutFilePath);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : userMap.entrySet()) {
                    bw.write(entry.getKey() + ":" + entry.getValue());
                    bw.newLine();
                }
                bw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

             */
        } catch (NullPointerException e) {
            setMessageTosend("Wrong format or account does not exist, try again");
        }

    }

    public void getProfile(String userId) {

        if (userMap.containsKey(userId)) {
            setUser(User.getInfo(get(userId), getApikey()));

            try {
                Collection<Artist> topArtistsColl = User.getTopArtists(get(userId), Period.OVERALL, getApikey());
                Collection<Track> topTracksColl = User.getTopTracks(get(userId), Period.OVERALL, getApikey());
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
            }
        } else setMessageTosend("You have to link an account using set command first");


    }

    public void deleteLinkedAccount(String userId) {
        String username = userMap.get(userId);
        System.out.println(username);
        userMap.remove(userId);
        setMessageTosend("Your linked account has been deleted");
    }


    public String getMessageTosend() {
        return messageTosend;
    }

    public void setMessageTosend(String messageTosend) {
        this.messageTosend = messageTosend;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAcc() {
        return userAcc;
    }

    public void setUserAcc(String userAcc) {
        this.userAcc = userAcc;
    }

    public String getApikey() {
        return apikey;
    }

    public void setApikey(String apikey) {
        this.apikey = apikey;
    }

    public String getRecievedMessage() {
        return recievedMessage;
    }

    public void setRecievedMessage(String recievedMessage) {
        this.recievedMessage = recievedMessage;
    }

    public String[] getArrMessage() {
        return arrMessage;
    }

    public void setArrMessage(String[] arrMessage) {
        this.arrMessage = arrMessage;
    }

    public String getNoUserNameMessage() {
        return noUserNameMessage;
    }

    public void setNoUserNameMessage(String noUserNameMessage) {
        this.noUserNameMessage = noUserNameMessage;
    }

    public EmbedBuilder getEmbedMessage() {
        return embedMessage;
    }

    public void setEmbedMessage(EmbedBuilder embedMessage) {
        this.embedMessage = embedMessage;
    }


    public void put(String discordId, String username) {
        userMap.put(discordId, username);
    }

    public String get(String discordId) {
        return userMap.get(discordId);
    }

    public boolean containsKey(String discordId) {
        return userMap.containsKey(discordId);
    }


    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public int getMaxTrackAmount() {
        return maxTrackAmount;
    }

    public void setMaxTrackAmount(int maxTrackAmount) {
        this.maxTrackAmount = maxTrackAmount;
    }

    public String[] getPages() {
        return pages;
    }

    public void setPages(String[] pages) {
        this.pages = pages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Period calculatePeriod(String periodString) {
        if (periodString.equalsIgnoreCase("week") || periodString.equalsIgnoreCase("w")) {
            return Period.WEEK;
        } else if (periodString.equalsIgnoreCase("month") || periodString.equalsIgnoreCase("m")) {
            return Period.ONE_MONTH;
        } else if (periodString.equalsIgnoreCase("3months") || periodString.equalsIgnoreCase("3m")) {
            return Period.THREE_MONTHS;
        } else if (periodString.equalsIgnoreCase("6months") || periodString.equalsIgnoreCase("6m")) {
            return Period.SIX_MONTHS;
        } else if (periodString.equalsIgnoreCase("year") || periodString.equalsIgnoreCase("y")) {
            return Period.TWELVE_MONTHS;
        } else if (periodString.equalsIgnoreCase("overall") || periodString.equalsIgnoreCase("alltime")) {
            return Period.OVERALL;
        } else return Period.WEEK;
    }
    /*
    @Override
    public void onGuildMessageReactionAdd(@Nonnull GuildMessageReactionAddEvent event) {
        if (event.getReactionEmote().getName().equals("➡") && !event.getMember().getUser().equals(event.getJDA().getSelfUser()) && !event.getMember().getUser().isBot() && event.getChannel().retrieveMessageById(event.getMessageId()).complete().getAuthor().getId().equals("678037870531051531")) {
            String[] checkIfFmCommand = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getTitle().split(" ");
            if (checkIfFmCommand[0].equalsIgnoreCase("Last")) {
                Caller.getInstance().setUserAgent("tst");
                String[] lastFmAccount = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getAuthor().getName().split("'");
                String username = lastFmAccount[0].substring(2);
                String[] periodFromEmbed = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getTitle().split(" ");
                String period = periodFromEmbed[1];
                Period periodFM = calculatePeriod(period);
                String[] totalPagesfromEmbed = event.getChannel().retrieveMessageById(event.getMessageId()).complete().getEmbeds().get(0).getFooter().getText().split(" ");
                String totalPages = totalPagesfromEmbed[3];
                String currentPage = totalPagesfromEmbed[1];
                String amountOfTracks = periodFromEmbed[3];
                System.out.println("username = " + username);
                System.out.println("period = " + period);
                System.out.println("currentpage = " + currentPage);
                System.out.println("totalpages = " + totalPages);
                System.out.println("total tracks = " + amountOfTracks);

                if (!currentPage.equalsIgnoreCase(totalPages)) {
                    setPage(0);
                    setPages(new String[Integer.parseInt(totalPages)]);
                    setUser(User.getInfo(username, getApikey()));
                    Collection<Track> topTracks = User.getTopTracks(username, periodFM, getApikey());
                    DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
                    formatSymbols.setDecimalSeparator('.');
                    formatSymbols.setGroupingSeparator(',');
                    DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
                    decimalFormat.setGroupingSize(3);
                    decimalFormat.setGroupingUsed(true);
                    EmbedBuilder topTracksBuilder = new EmbedBuilder();
                    LinkedList<Track> tracks = new LinkedList<>(topTracks);
                    int counter = 1;
                    if (Integer.parseInt(amountOfTracks) > tracks.size()) {
                    }
                    for (int i = 0; i <= Integer.parseInt(amountOfTracks) - 1; i++) {
                        String trackName = tracks.get(i).getName();
                        if (trackName.contains("*")) {
                            trackName = trackName.replace("*", "\\*");
                        }
                        String artistName = tracks.get(i).getArtist();
                        if (artistName.contains("*")) {
                            artistName = artistName.replace("*", "\\*");
                        }
                        String trackLink = tracks.get(i).getUrl();
                        int playedCount = tracks.get(i).getPlaycount();
                        String pageInformation = counter + ". " + artistName + " - [" + trackName + "](" + trackLink + ") (" + decimalFormat.format(playedCount) + " plays)" + "\n";
                        //topTracksBuilder.appendDescription(counter + ". " + artistName +  " - [" + trackName + "](" + trackLink + ") (" + decimalFormat.format(playedCount) + " plays)" + "\n");
                        counter++;
                        //pages = new String[(int)(Math.ceil(((float)tracks.size() )/ 20))+1];
                        if (i % 20 == 0 && i != 0) {
                            setPage(getPage() + 1);
                        }

                        if (i % 20 == 0) {
                            getPages()[getPage()] = "";
                        }
                        getPages()[getPage()] += pageInformation;

                    }

                    String thumbnail;
                    try {
                        thumbnail = tracks.get(0).getImageURL(ImageSize.MEDIUM);
                        if (thumbnail.equalsIgnoreCase("https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png")) {
                            Document doc = Jsoup.connect("https://www.last.fm/music/" + tracks.get(0).getArtist() + "/+images").userAgent("Chrome").get();
                            Elements images = doc.getElementsByClass("image-list-item-wrapper");
                            String imagelink = images.get(0).getElementsByTag("img").toString();
                            imagelink = imagelink.replace("/avatar170s/", "/300x300/");
                            imagelink += "</img>";

                            InputSource is = new InputSource(new StringReader(imagelink));
                            DOMParser dp = new DOMParser();
                            dp.parse(is);
                            org.w3c.dom.Document document = dp.getDocument();
                            NodeList nl = document.getElementsByTagName("img");
                            Node n = nl.item(0);
                            NamedNodeMap nnm = n.getAttributes();
                            thumbnail = nnm.getNamedItem("src").getFirstChild().getTextContent();
                        } else System.out.println("Image is not the defaulted one");
                    } catch (NullPointerException | IOException | SAXException e) {
                        thumbnail = "https://lastfm.freetls.fastly.net/i/u/64s/2a96cbd8b46e442fc41c2b86b821562f.png";
                        e.printStackTrace();
                    }
                    User user = User.getInfo(username, apikey);
                    topTracksBuilder.setAuthor("🎵 " + username + "'s Top tracks", user.getUrl());
                    topTracksBuilder.setThumbnail(thumbnail);
                    topTracksBuilder.setTitle("Last week top " + amountOfTracks);
                    topTracksBuilder.setColor(0xFF0000);
                    topTracksBuilder.appendDescription(getPages()[Integer.parseInt(currentPage)]);
                    topTracksBuilder.setFooter("Page " + (Integer.parseInt(currentPage) + 1) + " of " + totalPages);
                    event.getChannel().retrieveMessageById(event.getMessageId()).complete().editMessage(topTracksBuilder.build()).queue();


                    event.getReaction().removeReaction(event.getUser()).queue();
                }
            } else event.getReaction().removeReaction(event.getUser()).queue();
        }


        EmbedBuilder nextPage = new EmbedBuilder();

        //event.getChannel().retrieveMessageById(event.getMessageId()).complete().editMessage(nextPage.build()).queue();
        //nextPageOfTopTracks();
    }

     */


    public void readTextFileHashMap() {
        final String filePath = "D:/lastfmaccounts.txt";
        BufferedReader br;
        try {
            File file = new File(filePath);
            br = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] arrParts = line.split(":");
                String discordId = arrParts[0].trim();
                String userName = arrParts[1].trim();

                if (!discordId.equals("") && !userName.equals("")) {
                    if (!userMap.containsKey(discordId)) {
                        userMap.put(discordId, userName);
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void nextPageOfTopTracks() {

        //setUser(User.getInfo(get(discordId), getApikey()));
        //Collection<Track> topTracks = User.getTopTracks(get(discordId), period, apikey);
        DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        formatSymbols.setDecimalSeparator('.');
        formatSymbols.setGroupingSeparator(',');
        DecimalFormat decimalFormat = new DecimalFormat("#.##", formatSymbols);
        decimalFormat.setGroupingSize(3);
        decimalFormat.setGroupingUsed(true);
        //LinkedList<Track> tracks = new LinkedList<>(topTracks);
        EmbedBuilder topTracksBuilder = getEmbedMessage();
        String thumbnail = "";
        //topTracksBuilder
        System.out.println("something terribly wrong");
    }
}
