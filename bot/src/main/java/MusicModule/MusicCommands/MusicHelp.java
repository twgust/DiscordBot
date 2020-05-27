package MusicModule.MusicCommands;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class MusicHelp extends Command {
    private EmbedBuilder embedBuilder;

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.YELLOW);
        embedBuilder.setTitle("Music Module 🎶", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/MusicModule");
        embedBuilder.setDescription("https://github.com/twgust/");
        embedBuilder.addField("%play <song>", "plays a song", true);
        embedBuilder.addField("%skip", "skips", true);
        embedBuilder.addField("%queue", "returns queue", true);
        embedBuilder.addField("%song", "currently playing song", true);
        embedBuilder.addField("%pause", "pauses the music", true);
        embedBuilder.addField("%resume", "resumes the music", true);
        embedBuilder.addField("%search <song>", "searches youtube and returns results", false);
        embedBuilder.setImage("https://i.imgur.com/Z2DZ2p5.jpg");
        embedBuilder.setFooter("DM @Goose#0068 for support");

        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
