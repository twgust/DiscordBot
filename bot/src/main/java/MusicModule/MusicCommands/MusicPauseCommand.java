package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class MusicPauseCommand extends Command {
    private MusicController musicController;

    public MusicPauseCommand(MusicController controller){
        this.musicController = controller;
    }


    @Override
    public void execute(GuildMessageReceivedEvent event){
        musicController.getPlayer().setPaused(true);
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.YELLOW);
        builder.setTitle("Paused: " +
                musicController.getPlayer().getPlayingTrack().getInfo().title + " ! "
                , musicController.getPlayer().getPlayingTrack().getInfo().uri);
        builder.setFooter("%music for help");
        event.getChannel().sendMessage(builder.build()).queue();


    }
}
