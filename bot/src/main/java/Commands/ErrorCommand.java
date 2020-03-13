package Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

/**
 * Error class for throwing exceptions
 */
public class ErrorCommand{
    /**
     * Tells user if a command wasn't found
     * @param event takes in the event to send it back in the correct channel
     */
    public void throwMissingCommand(GuildMessageReceivedEvent event){
        event.getChannel().sendMessage("Command not found...").queue();
    }
}
