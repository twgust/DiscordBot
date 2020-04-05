package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.MusicController;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicPauseCommand extends Command {
    private MusicController controller;

    public MusicPauseCommand(MusicController controller){
        this.controller = controller;
    }


    @Override
    public void execute(GuildMessageReceivedEvent event){
        controller.getPlayer().setPaused(true);
        event.getChannel().sendMessage("Player has been paused");

    }
}
