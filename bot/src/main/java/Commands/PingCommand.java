package Commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;
import net.dv8tion.jda.api.entities.Activity.Timestamps;

public class PingCommand extends Command{
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong: " + event.getResponseNumber()).queue();
    }
}
