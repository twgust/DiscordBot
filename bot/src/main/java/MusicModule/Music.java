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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.sound.midi.Track;
import java.util.ArrayList;
//test commit
public class Music extends Command {
    AudioPlayerManager playerManager;
    TrackScheduler trackScheduler;
    AudioPlayer player;

    public Music(){
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        player = playerManager.createPlayer();
        trackScheduler = new TrackScheduler(player);
    }
    /** Takes string "identifier" passes it into the load item method parameter. This can be url or a song title
    e.g Dancing in the dark - Bruce Springsteen
     */
    public void loadTrack(GuildMessageReceivedEvent event, String identifier){
        identifier = event.getMessage().getContentRaw();

        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackScheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for(AudioTrack track: playlist.getTracks()){
                    trackScheduler.queue(track);
                }
            }
            //error messages can be put in the ErrorCommand Class
            @Override
            public void noMatches() {
                event.getChannel().sendMessage("We've got nothing, try again");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage("Something exploded, load failed.");

            }
        });
    }

}
