package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.MusicController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class MusicStopCommand extends Command {
    private MusicController musicController;

    public MusicStopCommand(MusicController musicController){
        this.musicController = musicController;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        int size = musicController.getScheduler().getQueue().size();

        for (int i = 0; i <= size ; i++) {
            musicController.getScheduler().nextTrack();
        }

        Guild guild = event.getGuild();
        guild.getAudioManager().closeAudioConnection();

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.YELLOW);
        builder.setTitle("Session stopped");
        builder.setDescription("Thank you for choosing <botname> !");
        builder.setImage("https://i.imgur.com/gKKW6bp.gif");
        builder.setFooter("%music for help");
        event.getChannel().sendMessage(builder.build()).queue();
    }
}
