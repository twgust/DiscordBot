package Commands;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class PingCommand extends Command{
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        MessageChannel channel = event.getChannel();
        long time = System.currentTimeMillis();
        channel.sendMessage("Pong: " + time).queue(response->{
            response.editMessageFormat("Pong: %d ms", (System.currentTimeMillis() - time));
        });
    }
}
