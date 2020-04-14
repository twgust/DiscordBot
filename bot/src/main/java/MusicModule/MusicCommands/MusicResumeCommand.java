package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicResumeCommand extends Command {
    private MusicController controller;

    public MusicResumeCommand(MusicController controller){
        this.controller = controller;
    }
    @Override
    public void execute(GuildMessageReceivedEvent event){
        controller.getPlayer().setPaused(false);
        event.getChannel().sendMessage("```Music has been resumed!```").queue();
    }

}
