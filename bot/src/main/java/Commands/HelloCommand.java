package Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class HelloCommand implements ICommand {
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("Hello").queue();
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
