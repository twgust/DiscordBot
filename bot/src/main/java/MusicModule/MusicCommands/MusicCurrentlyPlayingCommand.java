package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.Controller.MusicController;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class MusicCurrentlyPlayingCommand extends Command {
    private MusicController musicController;

    public MusicCurrentlyPlayingCommand(MusicController controller) {
        this.musicController = controller;
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        AudioTrack track = musicController.getPlayer().getPlayingTrack();
        long length = track.getInfo().length;
        length = length / 1000;
        length = length / 60;

        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.YELLOW);
        builder.setTitle("Current song: " + musicController.getPlayer().getPlayingTrack().getInfo().title,
                musicController.getPlayer().getPlayingTrack().getInfo().uri);
        builder.setFooter("%music for help");
        event.getChannel().sendMessage(builder.build()).queue();
    }
}
