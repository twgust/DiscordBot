package MusicModule.MusicCommands;

import Commands.Command;
import MusicModule.MusicController;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicCommand extends Command {

    private String helpText = "```~  🎷 Music Module 🎷 ~ " +
            "\n%play <song> - searches youtube and plays first result" +
            "\n%pause - pauses current song" +
            "\n%resume - resumes current song" +
            "\n%queue - displays current song queue" +
            "\n%song - display currently playing song" +
            "\n%skip - skips currently playing song" +
            "\n%search - returns first 4 search results from youtube```";

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage(helpText).queue();
    }
    public String getHelp(){
        return helpText;
    }

}
