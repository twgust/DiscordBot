package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import MusicModule.Controller.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class MusicResumeCommand extends Command {
    private MusicController musicController;

    public MusicResumeCommand(MusicController controller){
        this.musicController = controller;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event){
        musicController.getPlayer().setPaused(false);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.YELLOW);
        builder.setTitle(" Resumed playing: " +
                musicController.getPlayer().getPlayingTrack().getInfo().title + " ! "
                ,
                musicController.getPlayer().getPlayingTrack().getInfo().uri);
        builder.setFooter("%music for help");

        event.getChannel().sendMessage(builder.build()).queue();
        builder.clear();
    }
}
