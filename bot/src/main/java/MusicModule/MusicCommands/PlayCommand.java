package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.AudioPlayerSendHandler;
import MusicModule.TrackScheduler;
import com.gargoylesoftware.htmlunit.javascript.host.html.Audio;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

//git
public class PlayCommand extends Command {
    private  AudioPlayerManager playerManager;
    private TrackScheduler scheduler;
    private AudioPlayer player;
    private  Guild server;

    public PlayCommand() {

        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);

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
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {
            /*

             */
            @Override
            public void trackLoaded(AudioTrack track) {
                connectToVoiceChannel(server.getAudioManager());
                player.setVolume(100);
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
    public void execute(GuildMessageReceivedEvent event){
        String message = event.getMessage().getContentRaw();
        Member user = event.getMember();
        Guild server = event.getGuild();

        AudioSourceManagers.registerRemoteSources(playerManager);
        server.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));

        String[] array = message.split(" ", 2);
        loadMusic(array[1],user);
    }
}