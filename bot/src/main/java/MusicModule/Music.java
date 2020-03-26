package MusicModule;
//git
import Commands.Command;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.managers.AudioManager;

/**
 * TODO sendhelpmessage
 * TODO forceskipping, timestamp, reset, gettrackmanager, loadtrack
 */
public class Music extends Command {

    private final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private TrackScheduler scheduler;
    private AudioPlayer player;

    public Music(Guild server) {
        player = playerManager.createPlayer();
        server.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
        AudioSourceManagers.registerRemoteSources(playerManager);

    }

    public Music() {

    }

    public AudioPlayer getPlayer(){
        return player;
    }
    public void playerClosed(Guild server, GuildVoiceLeaveEvent event){

    }

    //bot only joins voice channels named "General" as of now. Will fix later TODO
    private void connectToVoiceChannel(AudioManager audioManager){
        if(!audioManager.isConnected() && !audioManager.isAttemptingToConnect()){
            for(VoiceChannel voiceChannel: audioManager.getGuild().getVoiceChannels()){
                if("General".equals(voiceChannel.getName())){
                    audioManager.openAudioConnection(voiceChannel);
                    return;
                }
            }
        }
    }
    /*
        LoadMusic tar in en låt från användare (identifier)
     */
    public void loadMusic(String identifier, Member user){
        if(user.getVoiceState().getChannel() == null){
            System.out.println("you are not in a voice channel");
        }
        else {
            System.out.println("you are in a voice channel");
        }

        //checks to see if user is a member of guild
        Guild server = user.getGuild();

        playerManager.loadItemOrdered(server, identifier, new AudioLoadResultHandler() {

             /*

              */
            @Override
            public void trackLoaded(AudioTrack track) {
                connectToVoiceChannel(server.getAudioManager());
                player.setVolume(25);
                scheduler.addToQueue(track, user);

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                System.out.println("playlist loaded successfully");
            }

            @Override
            public void noMatches() {
                System.out.println("no matches");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                System.out.println("load failed" + exception.getMessage());
            }
        });
    }
}

