package MusicModule;
//git

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

//todo fånga lite errors
//TODO skapa en lista/kö för musikmodulen, skriva addToQueue metoden
public class TrackScheduler extends AudioEventAdapter {
    private AudioPlayer player;
    private BlockingDeque<AudioTrack> queue;
    public TrackScheduler(AudioPlayer player) {

        this.player = player;
        this.queue = new LinkedBlockingDeque<>(); //FIFO principle, first in first out

    }

    public TrackScheduler() {

    }


    public void onNextSong(GuildMessageReceivedEvent event, AudioTrack track) {
        event.getChannel().sendMessage("Now plaing:" + track.getInfo().title);
    }

    public BlockingDeque<AudioTrack> getQueue() {
        return queue;
    }


    public void printelements() {
        System.out.println("ELEMENTS: + " + queue.size() + "Track: + " + queue.element());
    }

    /**
     * Function "addToQueue" is invoked every time a user types "%play + identifier"
     * It adds the identifier sent as a track to a queue. It is placed first in the queue.
     * If no track is currently being played the nextTrack function is invoked.
     * <p>
     * If a song is already playing it simply adds it to the last position of the queue.
     *
     * @param track
     * @param user
     */
    public void addToQueue(AudioTrack track, Member user) {
        queue.addLast(track);
        printelements();

        if (player.getPlayingTrack() == null) {
            nextTrack();
        }
    }

    public void addPlaylistToQueue(AudioPlaylist playlist, Member user) {
        queue.addAll(playlist.getTracks());
        System.out.println("Queue has now added" + playlist.getTracks());
    }


    public void nextTrack(){
        player.startTrack(queue.pollFirst(), false);
    }

    public void play(AudioTrack track, boolean clearQueue) {
        if (clearQueue) {
            queue.clear();
        }
        queue.add(track);
        nextTrack();
    }

    public void skip(AudioTrack track){
        player.playTrack(queue.pollFirst());
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        player.setPaused(true);
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        player.setPaused(false);
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        track = queue.element();
        System.out.println(track.getIdentifier());


    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            nextTrack();

        }
        if (AudioTrackEndReason.FINISHED.mayStartNext) {
            //nextTrack(true);
            System.out.println("FINISHED");

        }//here in light mie a problem
        if (AudioTrackEndReason.REPLACED.mayStartNext) {
            System.out.println("REPLACED");
            nextTrack();
        }
        if (AudioTrackEndReason.CLEANUP.mayStartNext) {
            System.out.println("CLEANUP");
            nextTrack();
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
