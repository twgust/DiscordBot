package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.MusicController;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicSkipCommand extends Command {
    private MusicController controller;


    public MusicSkipCommand(MusicController controller){
        this.controller = controller;

    }
    @Override
    public void execute(GuildMessageReceivedEvent event){
        String songTitle = controller.getPlayer().getPlayingTrack().getInfo().title;
        String songURI = controller.getPlayer().getPlayingTrack().getInfo().uri;
        String formatedSongTitle = ("```Song: " + songTitle + " has beeen skipped```");
        event.getChannel().sendMessage(formatedSongTitle).queue();

        controller.getScheduler().nextTrack();
    }
}
