package MusicModule;

import Commands.Command;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.*;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//git
public class MusicController extends Command {
    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

    public TrackScheduler getScheduler() {
        return scheduler;
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public Guild getServer() {
        return server;
    }

    public GuildMessageReceivedEvent getEvent() {
        return event;
    }

    private AudioPlayerManager playerManager;
    private TrackScheduler scheduler;
    private AudioPlayer player;
    private Guild server;
    private GuildMessageReceivedEvent event;
    private EventWaiter waiter;
    private final String one = "1️⃣";
    private final String two = "2️⃣";
    private final String three = "3️⃣";
    private final String four = "4️⃣";



    private ArrayList<AudioTrack> listOfTracks;

    //Constructor
    public MusicController(EventWaiter waiter) {
        this.waiter = waiter;
        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    //connects to voice channels with priority:
    //Users current voice channel (if it exists)
    //Servers Music voice channel (if it exists)
    //Servers General voice channel (if it exists)
    //Else music won't load
    private void connectToVoiceChannels(AudioManager audioManager, Member user){
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()){
            for(VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()){
                if(user.getVoiceState().getChannel() != null){
                    audioManager.openAudioConnection(user.getVoiceState().getChannel());
                }
                else if("Music".equals(voiceChannel.getName())){
                    audioManager.openAudioConnection(voiceChannel);
                }
                else if("General".equals(voiceChannel.getName())){
                    audioManager.openAudioConnection(voiceChannel);
                }
            }
        }
    }
    /**
     * function for searching music
     */
    public void searchMusic(String identifier, GuildMessageReceivedEvent event){
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                event.getChannel().sendMessage("Track: " + audioTrack.getInfo().title);
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
                try{
                    event.getChannel().sendMessage("```Track 1: " + listOfTracks.get(0).getInfo().title +
                            " " + listOfTracks.get(0).getInfo().uri +
                            "\nTrack 2: " + listOfTracks.get(1).getInfo().title + " " + listOfTracks.get(1).getInfo().uri +
                            "\nTrack 3: " + listOfTracks.get(2).getInfo().title + " " + listOfTracks.get(2).getInfo().uri +
                            "\nTrack 4: " + listOfTracks.get(3).getInfo().title + " " + listOfTracks.get(3).getInfo().uri +
                            "\nReact to start playing!✌✌```" ).queue(message -> {
                                initWaiter(message.getIdLong(), message.getChannel(), listOfTracks);

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
     *Function that loads all music. Function invoked by %play command.
     * @param identifier
     * @param user
     * @param event
     */
    public void loadMusic(String identifier, Member user, GuildMessageReceivedEvent event) {
        if (user.getVoiceState().getChannel() == null) {
            System.out.println("you are not in a voice channel");
        } else {
        }
        //checks to see if user is a member of guild
        Guild server = user.getGuild();
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                connectToVoiceChannels(server.getAudioManager(), user);
                player.setVolume(100);

                if (player.getPlayingTrack() == null) {
                    scheduler.addToQueue(track, user);
                    event.getChannel().sendMessage("```Now playing: " + track.getInfo().title + " ```\n").queue();
                } else if (player.getPlayingTrack() != null) {
                    scheduler.addToQueue(track, user);
                    event.getChannel().sendMessage("```"+track.getInfo().title + " added to queue(" + scheduler.getQueue().size() + ")```").queue();

                }
            }

            /**
             * This method is invoked with the ytsearch identifier, unsure why.
             * For now it  will only load the first track of the search results,
             * if "AudioTrack track = playlist.getTracks().get(0);" is not there it will load and queue
             * 30~ of the same tracks when the user uses the command %play "I'm on fire"
             */
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                connectToVoiceChannels(server.getAudioManager(), user);
                AudioTrack track = playlist.getTracks().get(0);
                if (player.getPlayingTrack() == null) {
                    scheduler.addToQueue(track, user);
                    event.getChannel().sendMessage("```Now playing: " + track.getInfo().title + " ```\n").queue();
                } else if (player.getPlayingTrack() != null) {
                    scheduler.addToQueue(track, user);
                    event.getChannel().sendMessage("```"+track.getInfo().title + " added to queue(" + scheduler.getQueue().size() + ")```").queue();
                }
            }
            @Override
            public void noMatches() {
                event.getChannel().sendMessage("current syntax: %play <youtube-url>").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage(exception.getMessage()).queue();
            }
        });
    }
    //getter
    public ArrayList<AudioTrack> getListOfTracks() {
        return listOfTracks;
    }
    public void playerClosed(Guild server, GuildVoiceLeaveEvent event) {

    }

    public void initWaiter(long messageId, MessageChannel channel, ArrayList<AudioTrack> tracks){
        waiter.waitForEvent(MessageReactionAddEvent.class, e -> {
            User user = e.getUser();

            System.out.println(e.getReactionEmote().getName());
            return checkEmote(e.getReactionEmote().getName()) && !user.isBot() && e.getMessageIdLong() == messageId;
        }, (e) -> {
            handleReaction(tracks, e.getReactionEmote().getName(), channel, e.getMember());

        },30, TimeUnit.SECONDS, () ->{

        });
    }
    public boolean checkEmote(String emote){
        switch (emote){
            case one:
            case two:
            case three:
            case four:
                System.out.println(true);
                return true;
            default:
                System.out.println(false);
                return false;

        }
    }

    public void handleReaction(ArrayList<AudioTrack> tracks, String emote, MessageChannel channel, Member member ){
        if(emote.equalsIgnoreCase("1️⃣")){
            scheduler.addToQueue(tracks.get(0), member);
        }
        else if(emote.equalsIgnoreCase("2️⃣")){
            scheduler.addToQueue(tracks.get(1), member);
        }
        else if(emote.equalsIgnoreCase("3️⃣")){
            scheduler.addToQueue(tracks.get(2), member);
        }
        else if(emote.equalsIgnoreCase("4️⃣")){
            scheduler.addToQueue(tracks.get(3), member);
        }
        else System.out.println("failed to load something wrong monkaW");
    }

}



