package MusicModule.MusicCommands;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class MusicEmbedBuilderTest extends Command {
    private EmbedBuilder embedBuilder;

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Music Module", "https://github.com/twgust/DiscordBot/tree/master/bot/src/main/java/MusicModule");
        embedBuilder.setDescription("https://github.com/twgust/");
        embedBuilder.addField("%play <song>", "plays a song", true);
        embedBuilder.addField("%skip", "skips", true);
        embedBuilder.addField("%queue", "returns queue", true);
        embedBuilder.addField("%song", "currently playing song", true);
        embedBuilder.addField("%pause", "pauses the music", true);
        embedBuilder.addField("%resume", "resumes the music", true);
        embedBuilder.addField("%search <song>", "searches youtube and returns results", false);
        embedBuilder.setImage("https://i.imgur.com/pD8Pf2y.gif");
        embedBuilder.setFooter("DM @Goose#0068 for support");

        /*
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle("EmbedBuilderTest");
        embedBuilder.setDescription("description");
        embedBuilder.setThumbnail("https://i.imgur.com/lDWuMVz.gif");
        embedBuilder.setFooter("footer");
        embedBuilder.setAuthor("author: " + event.getAuthor().getName());
        embedBuilder.setImage("https://i.imgur.com/lDWuMVz.gif");
         */

        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }
}
