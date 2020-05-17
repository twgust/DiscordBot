package Commands.samples;

import Commands.Command;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class HelloCommand extends Command {
    @Override
    public void execute(GuildMessageReceivedEvent event) {
        event.getChannel().sendMessage("Hello there!").queue();
    }
}
