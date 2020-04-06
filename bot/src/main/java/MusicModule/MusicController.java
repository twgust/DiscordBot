package MusicModule;

import Commands.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

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

    public MusicController() {

        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }


    public void sendMessages(GuildMessageReceivedEvent e) {
        e.getChannel().sendMessage("");
    }


    public void playerClosed(Guild server, GuildVoiceLeaveEvent event) {

    }

    //bot only joins voice channels named "General" as of now. Will fix later TODO
    private void connectToVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                if ("test".equals(voiceChannel.getName())) {
                    audioManager.openAudioConnection(voiceChannel);
                    return;
                }
            }
        }
    }

    /*
        LoadMusic tar in en låt från användare (identifier)
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
                connectToVoiceChannel(server.getAudioManager());
                player.setVolume(100);

                if (player.getPlayingTrack() == null) {
                    scheduler.addToQueue(track, user);
                    event.getChannel().sendMessage("Now playing: " + track.getInfo().title + " !\n").queue();
                } else if (player.getPlayingTrack() != null) {
                    scheduler.addToQueue(track, user);
                    event.getChannel().sendMessage(track.getInfo().title + " Queue position(" + scheduler.getQueue().size() + ")").queue();

                }
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    scheduler.addToQueue(track, user);
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
}



