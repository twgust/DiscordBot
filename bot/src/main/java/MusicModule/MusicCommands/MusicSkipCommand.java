package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.*;


import MusicModule.Controller.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class MusicSkipCommand extends Command {
    private MusicController musicController;


    public MusicSkipCommand(MusicController musicController){
        this.musicController = musicController;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event){
        String songTitle = musicController.getPlayer().getPlayingTrack().getInfo().title;
        String songURI = musicController.getPlayer().getPlayingTrack().getInfo().uri;
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.YELLOW);
        builder.setTitle("Skipped: " +
                songTitle + " 👻", songURI);
        builder.setFooter("%music for help");

        event.getChannel().sendMessage(builder.build()).queue();
        musicController.getScheduler().nextTrack();
    }
}
