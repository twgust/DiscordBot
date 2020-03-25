package MusicModule;
//git
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.HashSet;
import java.util.Set;

public class MusicInfo {
    private final Member user;
    /**
     * skips med hashset
     */
    private AudioTrack track;
    private Set<String> skips;

    public MusicInfo(AudioTrack track, Member user) {
        this.track = track;
        this.skips = new HashSet<>();
        this.user = user;

    }

    public AudioTrack getTrack() {
        return track;
    }

    public int getSkips() {
        return skips.size();
    }

    public void skip(User user) {
        if (skips.contains(user.getId())) {
            System.out.println("you've already skipped");
        } else skips.add(user.getId());
    }

    //called on new trackplaying
    public void clearSkips() {
        skips.clear();
    }

    public Member getUser() {
        return user;
    }
}


