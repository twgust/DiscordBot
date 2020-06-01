package MusicModule.MusicCommands;

import Commands.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.util.Queue;

public class MusicCommand extends Command {
    private EmbedBuilder eBuilder;
    private String helpText = "```~  🎷 Music Module 🎷 ~ " +
            "\n%play <song> - searches youtube and plays first result" +
            "\n%pause - pauses current song" +
            "\n%resume - resumes current song" +
            "\n%queue - displays current song queue" +
            "\n%song - display currently playing song" +
            "\n%skip - skips currently playing song" +
            "\n%search <song> - returns search results from youtube. React and play !" +
            "\nDM goose#0068 if you have suggestions" +
            "\nd[link test](https://discordapp.com)```";

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(helpText).queue();
    }
    public String getHelp(){
        return helpText;
    }

}
