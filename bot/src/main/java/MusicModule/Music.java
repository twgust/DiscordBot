package MusicModule;

import Commands.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventListener;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import javax.sound.midi.Track;
import java.util.ArrayList;

public class Music extends Command {
    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    TrackScheduler trackScheduler;
    AudioPlayer player;

    public Music(){
        AudioSourceManagers.registerRemoteSources(playerManager);
        player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(player);


    }

}
