package MusicModule;

import Commands.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.sound.midi.Track;

/**
 * TODO sendhelpmessage
 * TODO forceskipping, timestamp, reset, gettrackmanager, loadtrack
 */
public class Music extends Command {

    private static final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    public Music() {

        AudioSourceManagers.registerRemoteSources(playerManager);
    }
    private AudioPlayer playerStart(Guild server){
        AudioPlayer player = playerManager.createPlayer();
        TrackScheduler scheduler = new TrackScheduler(player);
        player.addListener((AudioEventListener) playerManager);
        server.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        return player;

    }

    private void playerClosed(Guild server, GuildVoiceLeaveEvent event){

    }
    private AudioPlayer getPlayer(Guild server){
        AudioPlayer p;
        p = playerStart(server);
        return p;
    }


    private void loadMusic(String identifier, Member user, Message strmessage){
        //checks to see if user is a member of guild
        Guild server = user.getGuild();
        //has player
        getPlayer(server);
        playerManager.loadItemOrdered(server, identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {

            }

            @Override
            public void noMatches() {

            }

            @Override
            public void loadFailed(FriendlyException exception) {

            }
        })
    }



    //commands
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        AudioPlayer p;
        String currentSongPlaying = event.getMessage().getContentRaw();
        switch(currentSongPlaying){
            case "current":
            case "now playing":
            case"song":
            case "info":
                //returns current playing song
                break;

        }
        String commadsForActiveMusicBot = event.getMessage().getContentRaw();
        switch(commadsForActiveMusicBot){
            case "play":
            case "pause":
            case "resume":
            case "stop":
        }

    }
}
