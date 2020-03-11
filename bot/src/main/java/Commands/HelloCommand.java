package Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class HelloCommand extends Command{
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("Hello there!").queue();
    }
}
