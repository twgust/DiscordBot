package MusicModule;
//git
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

//TODO skapa en lista/kö för musikmodulen, skriva addToQueue metoden
public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final Queue<MusicInfo> queue;



    public TrackScheduler(AudioPlayer player){

        this.player = player;
        this.queue = new LinkedBlockingQueue<>(); //FIFO principle, first in first out

    }
    public void addToQueue(AudioTrack track, Member user){
        MusicInfo info = new MusicInfo(track, user);
        queue.add(info);
        if(player.getPlayingTrack() == null){
            player.playTrack(track);

            //debugging
            System.out.println("IDENTIFIER: " + track.getIdentifier() + "\n");
            System.out.println("INFO: " + track.getInfo() + "\n");
            System.out.println("TRACK STATE: " + track.getState() + "\n");
            System.out.println("TRACK DURATION: " + track.getDuration());

        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        player.setPaused(true);
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        player.setPaused(false);
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        MusicInfo info = queue.element();
        info.getUser().deafen(true);
        VoiceChannel vc = info.getUser().getVoiceState().getChannel();
        if(vc == null ){
            player.stopTrack();

        }
        else info.getUser().getGuild().getAudioManager().openAudioConnection(vc);

    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            // Start next track
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your addToQueue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        System.out.println("onTrackException");
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        System.out.println("onTrackStuck TrackScheduler");
    }
    public MusicInfo getTrackInfo(AudioTrack track){
        return queue.stream().filter(musicInfo -> musicInfo.getTrack().equals(track)).findFirst().orElse(null);
    }
}