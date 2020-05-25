package MusicModule;

import Commands.Command;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
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
    private GuildMessageReceivedEvent event;
    private EventWaiter waiter;
    private AudioPlayerSendHandler audioPlayerSendHandler;
    private ArrayList<AudioTrack> listOfTracks;

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

    private void connectToVoiceChannels(AudioManager audioManager, Member user) {
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
                    embedBuilder.setTitle("Search results - React to play ! 🎶");
                    embedBuilder.addField("1) " + listOfTracks.get(0).getInfo().title, "", false);
                    embedBuilder.addField("2)  " + listOfTracks.get(1).getInfo().title, "", false);
                    embedBuilder.addField("3)  " + listOfTracks.get(2).getInfo().title, "", false);
                    embedBuilder.addField("4)  " + listOfTracks.get(3).getInfo().title, "", false);
                    embedBuilder.setFooter("%music for help ! 🎶");
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
     * @param member the user
     * @param event
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
                } else if (player.getPlayingTrack() != null) {
                    scheduler.addToQueue(track, member);

                }
            }

            /**
             * This method is invoked with the ytsearch identifier because youtube loads 30~ tracks.
             * For now it  will only load the first track of the search results,
             * if "AudioTrack track = playlist.getTracks().get(0);" is not there it will load and queue
             * 30~ of the same tracks when the user uses the command %play "I'm on fire"
             */
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                connectToVoiceChannels(server.getAudioManager(), member);
                AudioTrack track = playlist.getTracks().get(0);
                StringBuilder stringBuilder = new StringBuilder(track.getInfo().uri);
                String youtubeImageUrl = "https://img.youtube.com/vi/" + track.getInfo().identifier + "/0.jpg";
                String jpg = stringBuilder.toString();

                if(identifier.contains("/playlist")){
                    for (int i = 0; i < playlist.getTracks().size(); i++) {
                        scheduler.addToQueue(playlist.getTracks().get(i),member);
                    }
                    if(player.getPlayingTrack() == null){
                        builder.setTitle("Now playing: " + track.getInfo().title, track.getInfo().uri);
                        builder.setDescription(timeFormatting(track.getInfo().length));
                        builder.setImage(youtubeImageUrl);
                        event.getChannel().sendMessage(builder.build()).queue();
                        builder.clear();
                    }
                    else if(player.getPlayingTrack() != null){
                        builder.setTitle("Added to Queue: " + track.getInfo().title, track.getInfo().uri);
                        builder.setDescription("Position in queue: " + scheduler.getQueue().size());
                        builder.setImage(youtubeImageUrl);

                        event.getChannel().sendMessage(builder.build()).queue();
                        builder.clear();
                    }
                }
                else if (player.getPlayingTrack() == null) {
                    scheduler.addToQueue(track, member);
                    builder.setTitle("Now playing: " + track.getInfo().title, track.getInfo().uri);
                    builder.setDescription(timeFormatting(track.getInfo().length));
                    builder.setImage(youtubeImageUrl);

                    event.getChannel().sendMessage(builder.build()).queue();
                    builder.clear();

                } else if (player.getPlayingTrack() != null) {
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
                event.getChannel().sendMessage("No matches, Use %play [song title/link]").queue();
            }
            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage(exception.getMessage()).queue();
            }
        });
    }

    /**
     * Function for formatting time for song duration printout
     *
     * @param milliseconds Parameter for each track
     * @return
     */
    public String timeFormatting(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        int seconds = (int) ((milliseconds / 1000) % 60);
        if (minutes <= 9 && seconds <= 9) {
            return "Duration: 0" + minutes + ":0" + seconds;
        } else if (minutes <= 9 && seconds > 10) {
            return "Duration: 0" + minutes + ":" + seconds;
        }
        return "Duration: " + minutes + ":" + seconds + " Minutes 🎶";
    }

    //Generic function for embedded messages(test, probably not that useful due to how different each case is)
    public void genericEmbeddedMessage(GuildMessageReceivedEvent event, AudioTrack track, EmbedBuilder builder) {
        builder.setColor(Color.YELLOW);
        builder.setTitle(track.getInfo().title, track.getInfo().uri);
        /**
         * rest would be specific for each function which makes this function quite useless
         */
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
     * @param messageId
     * @param channel
     * @param tracks
     * @param message
     * @param event
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
     * @param tracks
     * @param emote
     * @param channel
     * @param member
     * @param event
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



