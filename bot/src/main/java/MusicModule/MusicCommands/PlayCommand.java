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
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.managers.GuildManager;

//git
public class PlayCommand extends Command {
    private  AudioPlayerManager playerManager;
    private TrackScheduler scheduler;
    private AudioPlayer player;
    private  Guild server;
    private GuildMessageReceivedEvent event;
    public PlayCommand() {

        playerManager = new DefaultAudioPlayerManager();
        player = playerManager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }


    public void sendMessages(GuildMessageReceivedEvent e){
        e.getChannel().sendMessage("");
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
    public void loadMusic(String identifier, Member user, GuildMessageReceivedEvent event){
        if(user.getVoiceState().getChannel() == null){
            System.out.println("you are not in a voice channel");
        }
        else {

        }

        //checks to see if user is a member of guild
        Guild server = user.getGuild();
        playerManager.loadItem(identifier, new AudioLoadResultHandler() {

            @Override
            public void trackLoaded(AudioTrack track) {
                connectToVoiceChannel(server.getAudioManager());
                player.setVolume(100);

                if(player.getPlayingTrack() == null){
                    scheduler.addToQueue(track, user);
                    event.getChannel().sendMessage(user.getNickname() + " your song is now playing").queue();
                }
                else if (player.getPlayingTrack() != null){
                    scheduler.addToQueue(track, user);
                    event.getChannel().sendMessage("Track is queued, current position --> " + scheduler.getQueue().size()).queue();
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
                event.getChannel().sendMessage("no matches, try again");
            }
            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage(exception.getMessage());
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

        /**
         *  Element one in the array is the identifier that we are passing to the loadMusic function
         */

        loadMusic(array[1],user, event);


    }
}