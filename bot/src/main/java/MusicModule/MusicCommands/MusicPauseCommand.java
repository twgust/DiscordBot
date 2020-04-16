package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicPauseCommand extends Command {
    private MusicController musicController;

    public MusicPauseCommand(MusicController controller){
        this.musicController = controller;
    }


    @Override
    public void execute(GuildMessageReceivedEvent event){

        musicController.getPlayer().setPaused(true);
        event.getChannel().sendMessage("```Music has been paused \n%resume to start playing again```").queue();

//        musicController.getPlayer().setPaused(true);
//        event.getChannel().sendMessage("Player has been paused");

    }
}
