package MusicModule.MusicCommands;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class MusicHelpCommand extends Command {

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("```Commands for music module:\n" +
                "%play <song> - searches youtube and picks first result " +
                "\n%pause - pauses current song" +
                "\n%resume - resumes current song" +
                "\n%queue - returns info of current queue" +
                "\n%song - returns info of currently playing song" +
                "\n%skip - skips current song and starts next one in the queue```").queue();


    }
}
