package MusicModule;
//git

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

//todo fånga lite errors
//TODO skapa en lista/kö för musikmodulen, skriva addToQueue metoden
public class TrackScheduler extends AudioEventAdapter {
    private  AudioPlayer player;
    private  BlockingDeque<MusicInfo> queue;

    public TrackScheduler(AudioPlayer player) {


        this.player = player;
        this.queue = new LinkedBlockingDeque<>(); //FIFO principle, first in first out

    }
    public TrackScheduler(){

    }



//    public List<AudioTrack> drainQueue() {
//        List<AudioTrack> drainedQueue = new ArrayList<>();
//        queue.drainTo(drainedQueue);
//        return drainedQueue;
//    }
    public void printelements(){
        System.out.println("ELEMENTS: + " + queue.size()+ "\nTrack: + " + queue.element().getTrack()
                + "\n\n" + "User: " + queue.element().getUser());
    }

    public void addToQueue(AudioTrack track, Member user) {
        MusicInfo info = new MusicInfo(track,user);
        queue.addLast(info);
        printelements();

        if (player.getPlayingTrack() == null) {
            player.playTrack(track);
        }

    }

    public void nextTrack(boolean noInterrupt) {

        MusicInfo info = new MusicInfo(queue.pollFirst().getTrack(), queue.element().getUser());
        AudioTrack track = info.getTrack();
        Member user = info.getUser();
        player.playTrack(queue.pollFirst().getTrack());

    }

    public void play(AudioTrack track, boolean clearQueue) {
        if (clearQueue) {
            queue.clear();
        }
        queue.add((MusicInfo) track);
        nextTrack(false);
    }

    public void skip(Member user) {
        AudioTrack track = player.getPlayingTrack();

        MusicInfo info = new MusicInfo(track, user);
        try {
        if(queue.peek().equals(info)){
            queue.poll();
            if(player.getPlayingTrack() == null){
                player.playTrack(queue.poll().getTrack());
            }
        }}catch (NullPointerException e){
            System.out.println("Nullpointer exception in trackScheduler skip function");
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
           track = queue.element().getTrack();
           System.out.println(track.getIdentifier());


    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack(true);


        }
        if(AudioTrackEndReason.FINISHED.mayStartNext){
            nextTrack(true);
            System.out.println("FINISHED");
        }
        if(AudioTrackEndReason.REPLACED.mayStartNext){
            System.out.println("REPLACED");
            nextTrack(false);
        }
        if(AudioTrackEndReason.CLEANUP.mayStartNext){
            System.out.println("CLEANUP");
            nextTrack(false);
        }
        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        System.out.println("onTrackException");
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        System.out.println("onTrackStuck TrackScheduler");
    }
}

//    public MusicInfo getTrackInfo(AudioTrack track) {
//        return queue.stream().filter(musicInfo -> musicInfo.getTrack().equals(track)).findFirst().orElse(null);
//    }
