package MusicModule.Controller;

import MusicModule.AudioPlayerSendHandler;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//git
public class MusicController {
    private final String one = "1️⃣";
    private final String two = "2️⃣";
    private final String three = "3️⃣";
    private final String four = "4️⃣";
    private AudioPlayerManager playerManager;
    private TrackScheduler scheduler;
    private AudioPlayer player;
    private Guild server;
    private AudioTrack track;
    private GuildMessageReceivedEvent event;
    private EventWaiter waiter;
    private AudioPlayerSendHandler audioPlayerSendHandler;
    private EmbedBuilder builderLastFM;
    private ArrayList<AudioTrack> lastFMTracks = new ArrayList<AudioTrack>();
    private ArrayList<AudioTrack> listOfTracks;
    private int counter = 0;

    /**
     *
     * @param waiter
     */
    public MusicController(EventWaiter waiter) {
        this.waiter = waiter;
        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        audioPlayerSendHandler = new AudioPlayerSendHandler(player);
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);

        AudioSourceManagers.registerRemoteSources(playerManager);
    }
    /**
     * Connects to voice channels with priority:
     * Users current voice channel (if it exists)
     * Servers Music voice channel (if it exists)
     * Servers General voice channel (if it exists)
     * Else music won't load
     *
     * @param audioManager Handles connections to voice channels
     * @param user         represents the user who invoked the function
     */

    public void connectToVoiceChannels(AudioManager audioManager, Member user) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                if (user.getVoiceState().getChannel() != null) {
                    audioManager.openAudioConnection(user.getVoiceState().getChannel());
                } else if ("Music".equals(voiceChannel.getName())) {
                    audioManager.openAudioConnection(voiceChannel);
                } else if ("General".equals(voiceChannel.getName())) {
                    audioManager.openAudioConnection(voiceChannel);
                }
            }
        }
    }
    /**
     * function for searching music
     */
    public void searchMusic(String identifier, GuildMessageReceivedEvent event) {
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {

            public void trackLoaded(AudioTrack audioTrack) {
                //function never invoked as searchMusic() always creates an array of 4 tracks from youtube
            }
            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                System.out.println("playlistloaded() under searchmusic() invoked by %search <input> command");

                listOfTracks = new ArrayList<AudioTrack>(5);
                AudioTrack t;
                for (int i = 0; i < 4; i++) {
                    t = audioPlaylist.getTracks().get(i);
                    listOfTracks.add(t);
                }
                try {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setColor(Color.YELLOW);
                    StringBuilder descriptionBuilder = new StringBuilder();
                    for (int i = 0; i < listOfTracks.size(); i++) {
                        String trackTitle = listOfTracks.get(i).getInfo().title;
                        String trackURI = listOfTracks.get(i).getInfo().uri;
                        descriptionBuilder.append((i + 1) +" : [").append(trackTitle).append("](").append(trackURI)
                                .append(")").append("\n\n");
                    }
                    embedBuilder.setTitle("Search results - React to play ! 🎶");
                    embedBuilder.setDescription(descriptionBuilder);
                    event.getChannel().sendMessage(embedBuilder.build()).queue(message -> {
                        initWaiter(message.getIdLong(), message.getChannel(), listOfTracks, message, event);
                        message.addReaction("1️⃣").queue();
                        message.addReaction("2️⃣").queue();
                        message.addReaction("3️⃣").queue();
                        message.addReaction("4️⃣").queue();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void noMatches() {
                event.getChannel().sendMessage("no matches").queue();
            }

            @Override
            public void loadFailed(FriendlyException e) {
                event.getChannel().sendMessage("nothing tracks found").queue();
            }
        });
    }


    /**
     * Function that loads all music. Function invoked by %play command.
     *
     * @param identifier The string provided by a user
     * @param member the user who invoked the function
     * @param event The messages event (used to get the channel in which the function was invoked)
     */
    public void youtubeTrackLoaded(String identifier, Member member, GuildMessageReceivedEvent event) {
        EmbedBuilder builder = new EmbedBuilder();
        Guild server = member.getGuild();
        server.getAudioManager().setSendingHandler(getAudioPlayerSendHandler());
        builder.setColor(Color.YELLOW);
        builder.setFooter("%music for help");


        if (member.getVoiceState().getChannel() == null) {
            System.out.println("you are not in a voice channel");
        }

        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            //metoden som robert kallar i lastFM
            @Override
            public void trackLoaded(AudioTrack track) {
                connectToVoiceChannels(server.getAudioManager(), member);
                player.setVolume(100);

                if (player.getPlayingTrack() == null) {
                    scheduler.addToQueue(track, member);
                    String youtubeImageUrl = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";

                    builder.setTitle("Now playing: " + track.getInfo().title, track.getInfo().uri);
                    builder.setDescription(timeFormatting(track.getInfo().length));
                    builder.setImage(youtubeImageUrl);
                    event.getChannel().sendMessage(builder.build()).queue();
                    builder.clear();

                } else if (player.getPlayingTrack() != null) {
                    scheduler.addToQueue(track, member);

                    String youtubeImageUrl = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";

                    builder.setTitle("Added to Queue: " + track.getInfo().title, track.getInfo().uri);
                    builder.setDescription(timeFormatting(track.getInfo().length));
                    builder.setImage(youtubeImageUrl);
                    event.getChannel().sendMessage(builder.build()).queue();
                    builder.clear();
                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                connectToVoiceChannels(server.getAudioManager(), member);

                //for youtube search
                AudioTrack track = playlist.getTracks().get(0);

                String youtubeImageUrl = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";
                builder.setImage(youtubeImageUrl);

                //if identifier contains list or playlist it is highly likely that it is a youtube playlist.
                if(identifier.contains("/playlist")|| identifier.contains("&list")){
                    for (int i = 0; i < playlist.getTracks().size(); i++) {
                        scheduler.addToQueue(playlist.getTracks().get(i),member);
                        System.out.println("playlist test");
                    }
                    //now playing (if playlist is large enough this won't have time to load before
                    //the player starts playing tracks
                    if(player.getPlayingTrack() == null){
                        builder.setTitle("Now playing: " + track.getInfo().title, track.getInfo().uri);
                        builder.setDescription(timeFormatting(track.getInfo().length));

                        event.getChannel().sendMessage(builder.build()).queue();
                        builder.clear();
                    }
                    //added to queue
                    else if(player.getPlayingTrack() != null){
                        builder.setTitle("Added to Queue: " + track.getInfo().title, track.getInfo().uri);
                        builder.setDescription("Position in queue: " + scheduler.getQueue().size());

                        event.getChannel().sendMessage(builder.build()).queue();
                        builder.clear();
                    }
                }
                //ytsearch Now Playing
                else if (player.getPlayingTrack() == null) {
                    scheduler.addToQueue(track, member);
                    builder.setTitle("Now playing: " + track.getInfo().title, track.getInfo().uri);
                    builder.setDescription(timeFormatting(track.getInfo().length));

                    event.getChannel().sendMessage(builder.build()).queue();
                    builder.clear();

                }
                //ytsearch Added to queue
                else if (player.getPlayingTrack() != null) {
                    scheduler.addToQueue(track, member);
                    builder.setTitle("Added to Queue: " + track.getInfo().title, track.getInfo().uri);
                    builder.setDescription("Position in queue: " + scheduler.getQueue().size());
                    builder.setImage(youtubeImageUrl);
                    event.getChannel().sendMessage(builder.build()).queue();
                    builder.clear();
                }
            }
            @Override
            public void noMatches() {
                event.getChannel().sendMessage("No matches, Use %play [song title/URL]").queue();
            }
            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage(exception.getMessage()).queue();
            }
        });
    }
    public void lastFMTrackLoader(String identifier, Member member, GuildMessageReceivedEvent event){
        builderLastFM = new EmbedBuilder();
        builderLastFM.setTitle("LastFM top 5 tracks queued");
        builderLastFM.setColor(Color.YELLOW);
        builderLastFM.setFooter("Parprogrammering poggers");

        Guild server = member.getGuild();
        server.getAudioManager().setSendingHandler(getAudioPlayerSendHandler());

        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                    //impossible to invoke since a playlist is always invoked while using
                    //"ytsearch: + identifier" as an identifier.
                }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                AudioTrack track = audioPlaylist.getTracks().get(0);
                player.setVolume(100);
                connectToVoiceChannels(server.getAudioManager(), member);

                lastFMTracks.add(track);
                scheduler.addToQueue(track, member);
                counter++;
                placeHolder(event);
                }

            @Override
            public void noMatches() {
                //TODO add a printout (either dm or guild message)
                System.out.println("no matches");
            }
            @Override
            public void loadFailed(FriendlyException e) {
                //TODO add a printout (either dm or guild message)
                System.out.println("load failed");

            }
        });
    }

    public void placeHolder(GuildMessageReceivedEvent event){
        if(counter == 4){
            counter = 0;
            int number = lastFMTracks.size();
            for (int i = 0; i <= number ; i++) {
                builderLastFM.addField(lastFMTracks.get(i).getInfo().title, "Duration: " +
                        timeFormatting(lastFMTracks.get(i).getDuration()), false);
            }
            event.getChannel().sendMessage(builderLastFM.build()).queue();
            lastFMTracks.clear();
        }
    }

    /**
     * Function for formatting time for song duration printout
     * Invoked in multiple functions
     * @param milliseconds Parameter for each track
     * @return returns a formatted string to be used for printouts
     */
    public String timeFormatting(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        int seconds = (int) ((milliseconds / 1000) % 60);
        if (minutes <= 9 && seconds <= 9) {
            return "Duration: 0" + minutes + ":0" + seconds;
        }
        else if (minutes <= 9 && seconds > 10) {
            return "Duration: 0" + minutes + ":" + seconds;
        }
        return "Duration: " + minutes + " minutes";
    }

    //Generic function for embedded messages(test, probably not that useful due to how different each case is)
    public void genericEmbeddedMessage(GuildMessageReceivedEvent event, AudioTrack track, EmbedBuilder builder) {
        builder.setColor(Color.YELLOW);
        builder.setTitle(track.getInfo().title, track.getInfo().uri);

        //rest would be specific for each function which makes this function quite useless

    }

    //getter
    public ArrayList<AudioTrack> getListOfTracks() {
        return listOfTracks;
    }

    public void playerClosed(Guild server, GuildVoiceLeaveEvent event) {

    }
    public AudioPlayerSendHandler getAudioPlayerSendHandler() {
        return audioPlayerSendHandler;
    }

    /**
     *
     * @param messageId Unique ID for the message.
     * @param channel The channel in which the message is sent
     * @param tracks The list of tracks
     * @param message The message which is being sent
     * @param event The messages event (used to get the channel in which the function was invoked)
     */
    public void initWaiter(long messageId, MessageChannel channel, ArrayList<AudioTrack> tracks, Message message, GuildMessageReceivedEvent event) {
        waiter.waitForEvent(MessageReactionAddEvent.class, e -> {
            User user = e.getUser();
            return checkEmote(e.getReactionEmote().getName()) && !user.isBot() && e.getMessageIdLong() == messageId;
        }, (e) -> {
            handleReaction(tracks, e.getReactionEmote().getName(), channel, e.getMember(), event);
            message.clearReactions().queue();

        }, 30, TimeUnit.SECONDS, () -> {
        });
    }

    /**
     *
     * @param emote
     * @return
     */
    public boolean checkEmote(String emote) {
        switch (emote) {
            case one:
            case two:
            case three:
            case four:
                return true;
            default:
                System.out.println(false);
                return false;
        }
    }

    /**
     * code duplication necessary see @genericEmbeddedMessage() function
     * @param tracks A list of tracks
     * @param emote An emote as a listener
     * @param channel The channel which the message will be sent in
     * @param member The member whom invoked the function
     * @param event The messages event (used to get the channel in which the function was invoked)
     */
    public void handleReaction(ArrayList<AudioTrack> tracks, String emote, MessageChannel channel, Member member, GuildMessageReceivedEvent event
    ) {
        setServer(member.getGuild());
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.YELLOW);
        builder.setFooter("%music for help");

        if (emote.equalsIgnoreCase("1️⃣")) {
            connectToVoiceChannels(server.getAudioManager(), member);
            scheduler.addToQueue(tracks.get(0), member);
            builder.setTitle("Queued: " + tracks.get(0).getInfo().title, tracks.get(0).getInfo().uri);
            builder.setDescription("Duration: " + timeFormatting(tracks.get(0).getInfo().length));

            event.getChannel().sendMessage(builder.build()).queue();
        } else if (emote.equalsIgnoreCase("2️⃣")) {
            connectToVoiceChannels(server.getAudioManager(), member);
            scheduler.addToQueue(tracks.get(1), member);

            builder.setTitle("Queued: " + tracks.get(1).getInfo().title, tracks.get(1).getInfo().uri);
            builder.setDescription("Duration: " + timeFormatting(tracks.get(1).getInfo().length));

            event.getChannel().sendMessage(builder.build()).queue();
        } else if (emote.equalsIgnoreCase("3️⃣")) {
            connectToVoiceChannels(server.getAudioManager(), member);
            scheduler.addToQueue(tracks.get(2), member);
            builder.setTitle("Queued: " + tracks.get(2).getInfo().title, tracks.get(2).getInfo().uri);
            builder.setDescription("Duration: " + timeFormatting(tracks.get(2).getInfo().length));

            event.getChannel().sendMessage(builder.build()).queue();
        } else if (emote.equalsIgnoreCase("4️⃣")) {
            connectToVoiceChannels(server.getAudioManager(), member);
            scheduler.addToQueue(tracks.get(3), member);
            builder.setTitle("Queued: " + tracks.get(3).getInfo().title, tracks.get(3).getInfo().uri);
            builder.setDescription("Duration: " + timeFormatting(tracks.get(3).getInfo().length));

            event.getChannel().sendMessage(builder.build()).queue();
        } else System.out.println("failed to load something wrong monkaW poggers");
    }
    /**
     *
     * @return returns playerManager
     */
    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
    /**
     *
     * @return returns trackScheduler (used for MusicQueueCommand)
     */
    public TrackScheduler getScheduler() {
        return scheduler;
    }
    /**
     *
     * @return returns Player
     */
    public AudioPlayer getPlayer() {
        return player;
    }

    /**
     *
     * @param server sets the server of instance variable server (type Guild)
     */
    public void setServer(Guild server) {
        this.server = server;
    }
}



