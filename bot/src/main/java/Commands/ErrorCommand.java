package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

/**
 * Error class for throwing exceptions
 */
public class ErrorCommand{
    private EmbedBuilder eb = new EmbedBuilder();
    /**
     * Tells user if a command wasn't found
     * @param event takes in the event to send it back in the correct channel
     */
    public void throwMissingCommand(GuildMessageReceivedEvent event){
        eb.clear();
        eb.setTitle("Command not found");
        eb.setColor(Color.YELLOW);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    public void throwCommandTimeOut(GuildMessageReceivedEvent event){
        eb.clear();
        eb.setTitle("Command timed out. Please try again later.");
        eb.setColor(Color.YELLOW);
        event.getChannel().sendMessage(eb.build()).queue();
    }

    public void throwFailedMessageProcessing(GuildMessageReceivedEvent event){
        eb.clear();
        eb.setTitle("Failed to process your message. Please try again later.");
        eb.setColor(Color.YELLOW);
        event.getChannel().sendMessage(eb.build()).queue();
    }
}
