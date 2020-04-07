package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.MusicController;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicCurrentlyPlayingCommand extends Command {
    private MusicController controller;

    public MusicCurrentlyPlayingCommand(MusicController controller){
        this.controller = controller;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event) {
       AudioTrack track =  controller.getPlayer().getPlayingTrack();
       event.getChannel().sendMessage("```Currently playing: " + track.getInfo().title + "\nSource: " + track.getInfo().uri + "```").queue();
    }
}
